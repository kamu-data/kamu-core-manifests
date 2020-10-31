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
      |      subPath: data_*.csv
      |    read:
      |      kind: csv
      |      header: true
      |    preprocess:
      |      kind: sql
      |      engine: sparkSQL
      |      queries:
      |      - query: SELECT * FROM input
      |    merge:
      |      kind: snapshot
      |      primaryKey:
      |      - id
      |  vocab:
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
          source = DatasetSource.Root(
            fetch = FetchStep.Url(
              url = URI.create("ftp://kamu.dev/test.zip"),
              cache = Some(SourceCaching.Forever())
            ),
            prepare = Some(
              Vector(
                PrepStep.Decompress(
                  format = CompressionFormat.Zip,
                  subPath = Some("data_*.csv")
                )
              )
            ),
            read = ReadStep.Csv(
              header = Some(true)
            ),
            preprocess =
              ds.content.source.asInstanceOf[DatasetSource.Root].preprocess,
            merge = MergeStrategy.Snapshot(
              primaryKey = Vector("id")
            )
          ),
          vocab = Some(DatasetVocabulary(eventTimeColumn = Some("date")))
        )
      )
    )

    ds.content.source
      .asInstanceOf[DatasetSource.Root]
      .preprocess
      .get
      .engine should equal(
      "sparkSQL"
    )
  }

  val VALID_PATH_GLOB_FETCH =
    """
      |kind: filesGlob
      |path: /opt/x/*.txt
      |cache:
      |  kind: forever
      |order: byName
    """.stripMargin

  "YAML utils" should "successfully load glob fetch" in {
    val ds = yaml.load[FetchStep](VALID_PATH_GLOB_FETCH)

    ds should equal(
      FetchStep.FilesGlob(
        path = "/opt/x/*.txt",
        cache = Some(SourceCaching.Forever()),
        order = Some(SourceOrdering.ByName)
      )
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
      |    - com.naturalearthdata.countries.10m.admin0
      |    - com.naturalearthdata.countries.50m.admin0
      |    transform:
      |      kind: sql
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
          source = DatasetSource.Derivative(
            inputs = Vector(
              DatasetID("com.naturalearthdata.countries.10m.admin0"),
              DatasetID("com.naturalearthdata.countries.50m.admin0")
            ),
            transform =
              ds.content.source.asInstanceOf[DatasetSource.Derivative].transform
          )
        )
      )
    )

    ds.content.source
      .asInstanceOf[DatasetSource.Derivative]
      .transform
      .engine should equal("sparkSQL")
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
      |    - input1
      |    - input2
      |    transform:
      |      kind: sql
      |      engine: sparkSQL
      |      query: SELECT * FROM input1 UNION ALL SELECT * FROM input2
      |  outputSlice:
      |    hash: ffaabb
      |    interval: '[1970-01-01T00:00:00.000Z, 1970-01-01T00:00:00.000Z]'
      |    numRecords: 10
      |  outputWatermark: '1970-01-01T00:01:00.000Z'
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
          prevBlockHash = Some("ffeebbddaaeedd"),
          systemTime = Instant.ofEpochMilli(0),
          source = block.content.source,
          outputSlice = Some(
            DataSlice(
              hash = "ffaabb",
              interval = Interval.point(Instant.ofEpochSecond(0)),
              numRecords = 10
            )
          ),
          outputWatermark = Some(Instant.ofEpochSecond(60)),
          inputSlices = Some(
            Vector(
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
    )
  }

  val VALID_DATASET_SUMMARY =
    """
      |apiVersion: 1
      |kind: DatasetSummary
      |content:
      |  id: baz
      |  kind: root
      |  dependencies:
      |  - foo
      |  - bar
      |  lastPulled: '1970-01-01T00:02:00.000Z'
      |  dataSize: 1024
      |  numRecords: 100
    """.stripMargin

  it should "successfully load dataset summary manifest" in {
    val block = yaml.load[Manifest[DatasetSummary]](VALID_DATASET_SUMMARY)

    block should equal(
      Manifest(
        apiVersion = 1,
        kind = "DatasetSummary",
        content = DatasetSummary(
          id = DatasetID("baz"),
          kind = DatasetKind.Root,
          dependencies = Set(DatasetID("foo"), DatasetID("bar")),
          lastPulled = Some(Instant.ofEpochSecond(120)),
          dataSize = 1024,
          numRecords = 100
        )
      )
    )
  }
}
