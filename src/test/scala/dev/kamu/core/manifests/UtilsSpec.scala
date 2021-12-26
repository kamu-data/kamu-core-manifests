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

class UtilsSpec extends FlatSpec with Matchers {

  val VALID_ROOT_POLLING_DATASET =
    """
      |apiVersion: 1
      |kind: DatasetSnapshot
      |content:
      |  name: kamu.test
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
          name = DatasetName("kamu.test"),
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
      |  name: com.naturalearthdata.countries.admin0
      |  source:
      |    kind: derivative
      |    inputs:
      |    - name: com.naturalearthdata.countries.10m.admin0
      |    - name: com.naturalearthdata.countries.50m.admin0
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
          name = DatasetName("com.naturalearthdata.countries.admin0"),
          source = DatasetSource.Derivative(
            inputs = Vector(
              TransformInput(
                None,
                DatasetName("com.naturalearthdata.countries.10m.admin0")
              ),
              TransformInput(
                None,
                DatasetName("com.naturalearthdata.countries.50m.admin0")
              )
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
      |  prevBlockHash: ffeebbddaaeedd
      |  systemTime: '1970-01-01T00:00:00.000Z'
      |  source:
      |    kind: derivative
      |    inputs:
      |    - name: input1
      |    - name: input2
      |    transform:
      |      kind: sql
      |      engine: sparkSQL
      |      query: SELECT * FROM input1 UNION ALL SELECT * FROM input2
      |  outputSlice:
      |    dataLogicalHash: ffaabb
      |    dataPhysicalHash: ffaabb
      |    dataInterval:
      |      start: 0
      |      end: 9
      |  outputWatermark: '1970-01-01T00:01:00.000Z'
      |  inputSlices:
      |  - datasetID: input1
      |    blockInterval:
      |      start: aa
      |      end: bb
      |    dataInterval:
      |      start: 0
      |      end: 9
      |  - datasetID: input2
      |    blockInterval:
      |      start: cc
      |      end: dd
    """.stripMargin

  it should "successfully load metadata block manifest" in {
    val block = yaml.load[Manifest[MetadataBlock]](VALID_METADATA_BLOCK)

    block should equal(
      Manifest(
        apiVersion = 1,
        kind = "MetadataBlock",
        content = MetadataBlock(
          prevBlockHash = Some(Multihash("ffeebbddaaeedd")),
          systemTime = Instant.ofEpochMilli(0),
          source = block.content.source,
          outputSlice = Some(
            OutputSlice(
              dataLogicalHash = Multihash("ffaabb"),
              dataPhysicalHash = Multihash("ffaabb"),
              dataInterval = OffsetInterval(
                start = 0,
                end = 9
              )
            )
          ),
          outputWatermark = Some(Instant.ofEpochSecond(60)),
          inputSlices = Some(
            Vector(
              InputSlice(
                datasetID = DatasetID("input1"),
                blockInterval = Some(
                  BlockInterval(
                    start = Multihash("aa"),
                    end = Multihash("bb")
                  )
                ),
                dataInterval = Some(OffsetInterval(start = 0, end = 9))
              ),
              InputSlice(
                datasetID = DatasetID("input2"),
                blockInterval = Some(
                  BlockInterval(
                    start = Multihash("cc"),
                    end = Multihash("dd")
                  )
                ),
                dataInterval = None
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
      |  id: "did:odf:abcdef"
      |  name: baz
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
          id = DatasetID("did:odf:abcdef"),
          name = DatasetName("baz"),
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
