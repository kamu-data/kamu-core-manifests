package dev.kamu.core.manifests

import java.io.{InputStream, OutputStream}
import java.net.URI

import org.apache.hadoop.fs.Path

/** Describes the layout of the data repository on disk */
case class RepositoryVolumeMap(
  /** Directory to store downloaded data in before processing */
  downloadDir: Path,
  /** Directory to store cache information in */
  checkpointDir: Path,
  /** Root data set directory for ingested data */
  dataDirRoot: Path,
  /** Data set directory for derivative data */
  dataDirDeriv: Path
) {
  def toManifest: Manifest[RepositoryVolumeMap] = {
    Manifest(
      apiVersion = 1,
      kind = getClass.getSimpleName,
      content = this
    )
  }
}

object RepositoryVolumeMap {
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

  def load(inputStream: InputStream): RepositoryVolumeMap = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    load(dataString)
  }

  def load(dataString: String): RepositoryVolumeMap = {
    loadYamlOrThrow[RepositoryVolumeMap](dataString)
  }

  def save(obj: RepositoryVolumeMap, outputStream: OutputStream): Unit = {
    saveYaml(obj, outputStream)
  }

  def loadManifest(inputStream: InputStream): Manifest[RepositoryVolumeMap] = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    loadManifest(dataString)
  }

  def loadManifest(dataString: String): Manifest[RepositoryVolumeMap] = {
    val value = loadYamlOrThrow[Manifest[RepositoryVolumeMap]](dataString)
    // TODO: Validate the manifest version/kind
    value.copy(content = value.content)
  }

  def saveManifest(
    obj: Manifest[RepositoryVolumeMap],
    outputStream: OutputStream
  ): Unit = {
    saveYaml(obj, outputStream)
  }
}
