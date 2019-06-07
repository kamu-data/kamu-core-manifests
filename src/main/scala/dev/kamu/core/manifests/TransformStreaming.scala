package dev.kamu.core.manifests

import java.io.{InputStream, OutputStream}
import java.net.URI

import org.apache.hadoop.fs.Path

case class TransformStreaming(
  /** ID of the new derivative dataset */
  id: String,
  /** Datasets that will be used as sources for this derivative */
  inputs: Vector[TransformStreamingInput],
  /** Processing steps that shape the data */
  steps: Vector[ProcessingStepSQL] = Vector.empty,
  /** Spark partitioning scheme */
  partitionBy: Vector[String] = Vector.empty
) {
  def toManifest: Manifest[TransformStreaming] = {
    Manifest(
      apiVersion = 1,
      kind = getClass.getSimpleName,
      content = this
    )
  }
}

case class TransformStreamingInput(
  /** ID of the input dataset */
  id: String,
  /*** Defines the mode in which this input should be open
    *
    * Valid values are:
    *  - batch
    *  - stream
    */
  mode: String = "stream"
)

object TransformStreaming {
  import pureconfig._
  import pureconfig.generic.auto._
  import pureconfig.generic.ProductHint
  import pureconfig.module.yaml.loadYamlOrThrow
  import dev.kamu.core.manifests.yaml.saveYaml

  implicit val pathReader = ConfigReader[String]
    .map(s => new Path(URI.create(s)))

  implicit val pathWriter = ConfigWriter[String]
    .contramap((p: Path) => p.toString)

  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](
      ConfigFieldMapping(CamelCase, CamelCase),
      useDefaultArgs = true,
      allowUnknownKeys = false
    )

  def load(inputStream: InputStream): TransformStreaming = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    load(dataString)
  }

  def load(dataString: String): TransformStreaming = {
    val value = loadYamlOrThrow[TransformStreaming](dataString)
    value
  }

  def save(obj: TransformStreaming, outputStream: OutputStream): Unit = {
    saveYaml(obj, outputStream)
  }

  def loadManifest(inputStream: InputStream): Manifest[TransformStreaming] = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    loadManifest(dataString)
  }

  def loadManifest(dataString: String): Manifest[TransformStreaming] = {
    val value = loadYamlOrThrow[Manifest[TransformStreaming]](dataString)
    // TODO: Validate the manifest version/kind
    value
  }

  def saveManifest(
    obj: Manifest[TransformStreaming],
    outputStream: OutputStream
  ): Unit = {
    saveYaml(obj, outputStream)
  }
}
