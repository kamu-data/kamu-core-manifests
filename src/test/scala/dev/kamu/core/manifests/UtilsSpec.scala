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
      |  source:
      |    kind: root
      |    fetch:
      |      kind: url
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
      |  vocabulary:
      |    eventTimeColumn: date
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[DatasetSnapshot]](VALID_ROOT_POLLING_DATASET)

    ds should equal(
      Manifest(
        apiVersion = 1,
        kind = "DatasetSnapshot",
        content = DatasetSnapshot(
          id = DatasetID("kamu.test"),
          source = SourceKind.Root(
            fetch = FetchSourceKind.Url(
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
            preprocess =
              ds.content.source.asInstanceOf[SourceKind.Root].preprocess,
            merge = MergeStrategyKind.Snapshot(
              primaryKey = Vector("id")
            )
          ),
          vocabulary = Some(DatasetVocabulary(eventTimeColumn = Some("date")))
        )
      )
    )

    ds.content.source
      .asInstanceOf[SourceKind.Root]
      .preprocessEngine should equal(
      Some("sparkSQL")
    )

  }

  val VALID_DERIVATIVE_STREAMING_DATASET =
    """
      |apiVersion: 1
      |kind: DatasetSnapshot
      |content:
      |  id: com.naturalearthdata.countries.admin0
      |  source:
      |    kind: derivative
      |    inputs:
      |    - id: com.naturalearthdata.countries.10m.admin0
      |    - id: com.naturalearthdata.countries.50m.admin0
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
          source = SourceKind.Derivative(
            inputs = Vector(
              SourceKind.Derivative.Input(
                DatasetID("com.naturalearthdata.countries.10m.admin0")
              ),
              SourceKind.Derivative.Input(
                DatasetID("com.naturalearthdata.countries.50m.admin0")
              )
            ),
            transform =
              ds.content.source.asInstanceOf[SourceKind.Derivative].transform
          )
        )
      )
    )

    ds.content.source
      .asInstanceOf[SourceKind.Derivative]
      .transformEngine should equal("sparkSQL")
  }

  val VALID_METADATA_BLOCK =
    """
      |apiVersion: 1
      |kind: MetadataBlock
      |content:
      |  blockHash: ddeeaaddbbeeff
      |  prevBlockHash: ffeebbddaaeedd
      |  systemTime: '1970-01-01T00:00:00.000Z'
      |  source:
      |    kind: derivative
      |    inputs:
      |    - id: input1
      |    - id: input2
      |    transform:
      |      engine: sparkSQL
      |      query: SELECT * FROM input1 UNION ALL SELECT * FROM input2
      |  outputSlice:
      |    hash: ffaabb
      |    interval: '[1970-01-01T00:00:00.000Z, 1970-01-01T00:00:00.000Z]'
      |    numRecords: 10
      |  outputExplicitWatermark: '1970-01-01T00:01:00.000Z'
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

    block should equal(
      Manifest(
        apiVersion = 1,
        kind = "MetadataBlock",
        content = MetadataBlock(
          blockHash = "ddeeaaddbbeeff",
          prevBlockHash = "ffeebbddaaeedd",
          systemTime = Instant.ofEpochMilli(0),
          source = block.content.source,
          outputSlice = Some(
            DataSlice(
              hash = "ffaabb",
              interval = Interval.point(Instant.ofEpochSecond(0)),
              numRecords = 10
            )
          ),
          outputExplicitWatermark = Some(Instant.ofEpochSecond(60)),
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
