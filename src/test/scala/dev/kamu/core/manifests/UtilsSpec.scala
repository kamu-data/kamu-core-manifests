/*
 * Copyright 2018 kamu.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.kamu.core.manifests

import java.net.URI
import java.time.Instant

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import dev.kamu.core.manifests.parsing.pureconfig.yaml
import yaml.defaults._
import pureconfig.generic.auto._

class UtilsSpec extends AnyFlatSpec with Matchers {

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
