package dev.kamu.core.manifests

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
      |  id: ca.vancouver.data.rapid-transit.lines
      |  rootPollingSource:
      |    url: ftp://webftp.vancouver.ca/OpenData/shape/shape_rapid_transit.zip
      |    format: shapefile
      |    subPathRegex: rapid_transit_line.*
      |    preprocess:
      |      - view: output
      |        query: SOME_SQL
    """.stripMargin

  "YAML utils" should "successfully load root dataset manifest" in {
    val ds = yaml.load[Manifest[Dataset]](VALID_ROOT_POLLING_DATASET)
    assert(
      ds == Manifest(
        apiVersion = 1,
        kind = "Dataset",
        content = Dataset(
          id = DatasetID("ca.vancouver.data.rapid-transit.lines"),
          rootPollingSource = Some(
            RootPollingSource(
              url = java.net.URI
                .create(
                  "ftp://webftp.vancouver.ca/OpenData/shape/shape_rapid_transit.zip"
                ),
              format = "shapefile",
              subPathRegex = Some("rapid_transit_line.*"),
              readerOptions = RootPollingSource.DEFAULT_READER_OPTIONS,
              preprocess = Vector(
                ProcessingStepSQL(
                  view = "output",
                  query = "SOME_SQL"
                )
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
