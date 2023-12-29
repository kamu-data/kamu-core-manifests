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
      |version: 1
      |kind: DatasetSnapshot
      |content:
      |  name: kamu.test
      |  kind: Root
      |  metadata:
      |    - kind: SetPollingSource
      |      fetch:
      |        kind: Url
      |        url: ftp://kamu.dev/test.zip
      |        cache:
      |          kind: Forever
      |      prepare:
      |        - kind: Decompress
      |          format: Zip
      |          subPath: data_*.csv
      |      read:
      |        kind: Csv
      |        header: true
      |      preprocess:
      |        kind: Sql
      |        engine: sparkSQL
      |        query: SELECT * FROM input
      |      merge:
      |        kind: Snapshot
      |        primaryKey:
      |          - id
      |    - kind: SetVocab
      |      eventTimeColumn: date
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[DatasetSnapshot]](VALID_ROOT_POLLING_DATASET)

    ds should equal(
      Manifest(
        version = 1,
        kind = "DatasetSnapshot",
        content = DatasetSnapshot(
          name = DatasetAlias("kamu.test"),
          kind = DatasetKind.Root,
          metadata = Vector(
            SetPollingSource(
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
              preprocess = Some(
                Transform.Sql(
                  engine = "sparkSQL",
                  query = Some("SELECT * FROM input"),
                  queries = None,
                  temporalTables = None
                )
              ),
              merge = MergeStrategy.Snapshot(
                primaryKey = Vector("id")
              )
            ),
            SetVocab(eventTimeColumn = Some("date"))
          )
        )
      )
    )
  }

  val VALID_PATH_GLOB_FETCH =
    """
      |kind: FilesGlob
      |path: /opt/x/*.txt
      |cache:
      |  kind: Forever
      |order: ByName
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
      |version: 1
      |kind: DatasetSnapshot
      |content:
      |  name: com.naturalearthdata.countries.admin0
      |  kind: Derivative
      |  metadata:
      |    - kind: SetTransform
      |      inputs:
      |        - datasetRef: did:odf:fed012126262ba49e1ba8392c26f7a39e1ba8d756c7469786d3365200c68402ff65da
      |          alias: com.naturalearthdata.countries.10m.admin0
      |        - datasetRef: did:odf:fed012126262ba49e1ba8392c26f7a39e1ba8d756c7469786d3365200c68402ff65dc
      |          alias: com.naturalearthdata.countries.50m.admin0
      |      transform:
      |        kind: Sql
      |        engine: sparkSQL
      |        queries:
      |          - alias: com.naturalearthdata.countries.admin0
      |            query: SOME_SQL
    """.stripMargin

  it should "successfully load derivative dataset manifest" in {
    val ds =
      yaml.load[Manifest[DatasetSnapshot]](VALID_DERIVATIVE_STREAMING_DATASET)

    ds should equal(
      Manifest(
        version = 1,
        kind = "DatasetSnapshot",
        content = DatasetSnapshot(
          name = DatasetAlias("com.naturalearthdata.countries.admin0"),
          kind = DatasetKind.Derivative,
          metadata = Vector(
            SetTransform(
              inputs = Vector(
                TransformInput(
                  DatasetRef(
                    "did:odf:fed012126262ba49e1ba8392c26f7a39e1ba8d756c7469786d3365200c68402ff65da"
                  ),
                  Some("com.naturalearthdata.countries.10m.admin0")
                ),
                TransformInput(
                  DatasetRef(
                    "did:odf:fed012126262ba49e1ba8392c26f7a39e1ba8d756c7469786d3365200c68402ff65dc"
                  ),
                  Some("com.naturalearthdata.countries.50m.admin0")
                )
              ),
              transform =
                ds.content.metadata(0).asInstanceOf[SetTransform].transform
            )
          )
        )
      )
    )
  }
}
