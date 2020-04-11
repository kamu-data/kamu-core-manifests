/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI
import java.time.Instant

import org.scalatest._
import dev.kamu.core.manifests.parsing.pureconfig.yaml
import yaml.defaults._
import pureconfig.generic.auto._
import spire.math.Interval

class UtilsSpec extends FlatSpec with Matchers {

  val VALID_ROOT_POLLING_DATASET =
    """
      |apiVersion: 1
      |kind: DatasetSnapshot
      |content:
      |  id: kamu.test
      |  rootPollingSource:
      |    fetch:
      |      kind: fetchUrl
      |      url: ftp://kamu.dev/test.zip
      |      cache:
      |        kind: forever
      |    prepare:
      |    - kind: decompress
      |      format: zip
      |      subPathRegex: data_*.csv
      |    read:
      |      kind: generic
      |      name: csv
      |      options:
      |        header: 'true'
      |    preprocess:
      |      engine: sparkSQL
      |      queries:
      |      - query: SELECT * FROM input
      |    merge:
      |      kind: snapshot
      |      primaryKey:
      |      - id
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[DatasetSnapshot]](VALID_ROOT_POLLING_DATASET)

    ds should equal(
      Manifest(
        apiVersion = 1,
        kind = "DatasetSnapshot",
        content = DatasetSnapshot(
          id = DatasetID("kamu.test"),
          rootPollingSource = Some(
            RootPollingSource(
              fetch = ExternalSourceKind.FetchUrl(
                url = URI.create("ftp://kamu.dev/test.zip"),
                cache = Some(CachingKind.Forever())
              ),
              prepare = Vector(
                PrepStepKind.Decompress(
                  format = "zip",
                  subPathRegex = Some("data_*.csv")
                )
              ),
              read = ReaderKind.Generic(
                name = "csv",
                options = Map("header" -> "true")
              ),
              preprocess = ds.content.rootPollingSource.get.preprocess,
              merge = MergeStrategyKind.Snapshot(primaryKey = Vector("id"))
            )
          )
        )
      )
    )

    ds.content.rootPollingSource.get.preprocessEngine should equal(
      Some("sparkSQL")
    )

  }

  val VALID_DERIVATIVE_STREAMING_DATASET =
    """
      |apiVersion: 1
      |kind: DatasetSnapshot
      |content:
      |  id: com.naturalearthdata.countries.admin0
      |  derivativeSource:
      |    inputs:
      |      - id: com.naturalearthdata.countries.10m.admin0
      |      - id: com.naturalearthdata.countries.50m.admin0
      |    transform:
      |      engine: sparkSQL
      |      queries:
      |      - alias: com.naturalearthdata.countries.admin0
      |        query: SOME_SQL
    """.stripMargin

  it should "successfully load derivative dataset manifest" in {
    val ds =
      yaml.load[Manifest[DatasetSnapshot]](VALID_DERIVATIVE_STREAMING_DATASET)

    ds should equal(
      Manifest(
        apiVersion = 1,
        kind = "DatasetSnapshot",
        content = DatasetSnapshot(
          id = DatasetID("com.naturalearthdata.countries.admin0"),
          derivativeSource = Some(
            DerivativeSource(
              inputs = Vector(
                DerivativeInput(
                  DatasetID("com.naturalearthdata.countries.10m.admin0")
                ),
                DerivativeInput(
                  DatasetID("com.naturalearthdata.countries.50m.admin0")
                )
              ),
              transform = ds.content.derivativeSource.get.transform
            )
          )
        )
      )
    )

    ds.content.derivativeSource.get.transformEngine should equal("sparkSQL")
  }

  val VALID_METADATA_BLOCK =
    """
      |apiVersion: 1
      |kind: MetadataBlock
      |content:
      |  blockHash: ddeeaaddbbeeff
      |  prevBlockHash: ffeebbddaaeedd
      |  systemTime: '1970-01-01T00:00:00.000Z'
      |  outputSlice:
      |    hash: ffaabb
      |    interval: '[1970-01-01T00:00:00.000Z, 1970-01-01T00:00:00.000Z]'
      |    numRecords: 10
      |  inputSlices:
      |  - hash: aa
      |    interval: '(1970-01-01T00:01:00.000Z, 1970-01-01T00:02:00.000Z]'
      |    numRecords: 10
      |  - hash: zz
      |    interval: '()'
      |    numRecords: 0
    """.stripMargin

  it should "successfully load metadata block manifest" in {
    val block = yaml.load[Manifest[MetadataBlock]](VALID_METADATA_BLOCK)
    assert(
      block == Manifest(
        apiVersion = 1,
        kind = "MetadataBlock",
        content = MetadataBlock(
          blockHash = "ddeeaaddbbeeff",
          prevBlockHash = "ffeebbddaaeedd",
          systemTime = Instant.ofEpochMilli(0),
          outputSlice = Some(
            DataSlice(
              hash = "ffaabb",
              interval = Interval.point(Instant.ofEpochMilli(0)),
              numRecords = 10
            )
          ),
          inputSlices = Vector(
            DataSlice(
              hash = "aa",
              interval = Interval
                .openLower(
                  Instant.ofEpochSecond(60),
                  Instant.ofEpochSecond(120)
                ),
              numRecords = 10
            ),
            DataSlice(
              hash = "zz",
              interval = Interval.empty,
              numRecords = 0
            )
          )
        )
      )
    )
  }
}
