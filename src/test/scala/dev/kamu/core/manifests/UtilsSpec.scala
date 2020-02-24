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

class UtilsSpec extends FlatSpec {

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
      |    - kind: sparkSQL
      |      query: SELECT * FROM input
      |    merge:
      |      kind: snapshot
      |      primaryKey:
      |      - id
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[DatasetSnapshot]](VALID_ROOT_POLLING_DATASET)
    assert(
      ds == Manifest(
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
              preprocess = Vector(
                ProcessingStepKind.SparkSQL(
                  alias = None,
                  query = "SELECT * FROM input"
                )
              ),
              merge = MergeStrategyKind.Snapshot(primaryKey = Vector("id"))
            )
          )
        )
      )
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
      |    steps:
      |      - kind: sparkSQL
      |        alias: com.naturalearthdata.countries.admin0
      |        query: SOME_SQL
    """.stripMargin

  it should "successfully load derivative dataset manifest" in {
    val ds =
      yaml.load[Manifest[DatasetSnapshot]](VALID_DERIVATIVE_STREAMING_DATASET)
    assert(
      ds == Manifest(
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
              steps = Vector(
                ProcessingStepKind.SparkSQL(
                  alias = Some("com.naturalearthdata.countries.admin0"),
                  query = "SOME_SQL"
                )
              )
            )
          )
        )
      )
    )
  }

  val VALID_METADATA_BLOCK =
    """
      |apiVersion: 1
      |kind: MetadataBlock
      |content:
      |  blockHash: ddeeaaddbbeeff
      |  prevBlockHash: ffeebbddaaeedd
      |  systemTime: '1970-01-01T00:00:00.000Z'
      |  outputDataInterval: '[1970-01-01T00:00:00.000Z, 1970-01-01T00:00:00.000Z]'
      |  outputDataHash: ffaabb
      |  inputDataIntervals:
      |  - id: A
      |    interval: '(1970-01-01T00:01:00.000Z, 1970-01-01T00:02:00.000Z]'
      |  - id: B
      |    interval: '()'
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
          outputDataInterval = Interval.point(Instant.ofEpochMilli(0)),
          outputDataHash = "ffaabb",
          inputDataIntervals = Vector(
            InputDataSlice(
              DatasetID("A"),
              Interval
                .openLower(
                  Instant.ofEpochSecond(60),
                  Instant.ofEpochSecond(120)
                )
            ),
            InputDataSlice(
              DatasetID("B"),
              Interval.empty
            )
          )
        )
      )
    )
  }
}
