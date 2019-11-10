package dev.kamu.core.manifests

import java.net.URI

import org.scalatest._
import dev.kamu.core.manifests.parsing.pureconfig.yaml
import yaml.defaults._
import pureconfig.generic.auto._

class UtilsSpec extends FlatSpec {

  val VALID_ROOT_POLLING_DATASET =
    """
      |apiVersion: 1
      |kind: Dataset
      |content:
      |  id: kamu.test
      |  rootPollingSource:
      |    fetch:
      |      kind: fetchUrl
      |      url: ftp://kamu.dev/test.zip
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
      |    - view: output
      |      query: SELECT * FROM input
      |    merge:
      |      kind: snapshot
      |      primaryKey:
      |      - id
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[Dataset]](VALID_ROOT_POLLING_DATASET)
    assert(
      ds == Manifest(
        apiVersion = 1,
        kind = "Dataset",
        content = Dataset(
          id = DatasetID("kamu.test"),
          rootPollingSource = Some(
            RootPollingSource(
              fetch = ExternalSourceFetchUrl(
                url = URI.create("ftp://kamu.dev/test.zip")
              ),
              prepare = Vector(
                PrepStepDecompress(
                  format = "zip",
                  subPathRegex = Some("data_*.csv")
                )
              ),
              read = ReaderGeneric(
                name = "csv",
                options = ReaderGeneric.DEFAULT_READER_OPTIONS + ("header" -> "true")
              ),
              preprocess = Vector(
                ProcessingStepSQL(
                  view = "output",
                  query = "SELECT * FROM input"
                )
              ),
              merge = MergeStrategySnapshot(
                primaryKey = Vector("id"),
                modificationIndicator = None
              )
            )
          )
        )
      )
    )
  }

  val VALID_DERIVATIVE_STREAMING_DATASET =
    """
      |apiVersion: 1
      |kind: Dataset
      |content:
      |  id: com.naturalearthdata.countries.admin0
      |  derivativeSource:
      |    inputs:
      |      - id: com.naturalearthdata.countries.10m.admin0
      |      - id: com.naturalearthdata.countries.50m.admin0
      |        mode: batch
      |    steps:
      |      - view: com.naturalearthdata.countries.admin0
      |        query: SOME_SQL
    """.stripMargin

  it should "successfully load derivative dataset manifest" in {
    val ds = yaml.load[Manifest[Dataset]](VALID_DERIVATIVE_STREAMING_DATASET)
    assert(
      ds == Manifest(
        apiVersion = 1,
        kind = "Dataset",
        content = Dataset(
          id = DatasetID("com.naturalearthdata.countries.admin0"),
          derivativeSource = Some(
            DerivativeSource(
              inputs = Vector(
                DerivativeInput(
                  DatasetID("com.naturalearthdata.countries.10m.admin0")
                ),
                DerivativeInput(
                  DatasetID("com.naturalearthdata.countries.50m.admin0"),
                  DerivativeInput.Mode.Batch
                )
              ),
              steps = Vector(
                ProcessingStepSQL(
                  view = "com.naturalearthdata.countries.admin0",
                  query = "SOME_SQL"
                )
              )
            )
          )
        )
      )
    )
  }
}