package dev.kamu.core.manifests

import java.io.InputStream
import java.net.URI

import org.apache.hadoop.fs.Path
import pureconfig.generic.ProductHint
import pureconfig.module.yaml.loadYamlOrThrow
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigReader}

case class DataSourcePolling(
  /** Unique identifier of the dataset */
  id: String,
  /** Data source location */
  url: URI,
  /** Name of a compression algorithm used on data */
  compression: Option[String] = None,
  /** Path to a data file within a multi-file archive */
  subPath: Option[Path] = None,
  /** Regex for finding desired data file within a multi-file archive */
  subPathRegex: Option[String] = None,
  /** A raw data format (as supported by Spark's read function) */
  format: String,
  /** Options to pass into the [[org.apache.spark.sql.DataFrameReader]] */
  readerOptions: Map[String, String] = Map.empty,
  /** A DDL-formatted schema.
    *
    * Schema can be used to coerce values into more appropriate data types.
    */
  schema: Vector[String] = Vector.empty,
  /** Pre-processing steps to shape the data */
  preprocess: Vector[ProcessingStepSQL] = Vector.empty,
  /** One of the supported merge strategies (see [[MergeStrategyKind]]) */
  mergeStrategy: MergeStrategyKind = Append(),
  /** Collapse partitions of the result to specified number.
    *
    * If zero - the step will be skipped
    */
  coalesce: Int = 1
) {
  def withDefaults(): DataSourcePolling = {
    copy(
      readerOptions =
        DataSourcePolling.DEFAULT_READER_OPTIONS ++ readerOptions
    )
  }
}

object DataSourcePolling {
  import pureconfig.generic.auto._

  val DEFAULT_READER_OPTIONS: Map[String, String] = Map(
    "mode" -> "FAILFAST"
  )

  implicit val pathReader = ConfigReader[String]
    .map(s => new Path(URI.create(s)))

  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](
      ConfigFieldMapping(CamelCase, CamelCase),
      useDefaultArgs = true,
      allowUnknownKeys = false
    )

  def load(inputStream: InputStream): DataSourcePolling = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    load(dataString)
  }

  def load(dataString: String): DataSourcePolling = {
    val value = loadYamlOrThrow[DataSourcePolling](dataString)
    value.withDefaults()
  }

  def loadManifest(inputStream: InputStream): Manifest[DataSourcePolling] = {
    val dataString = scala.io.Source.fromInputStream(inputStream).mkString
    loadManifest(dataString)
  }

  def loadManifest(dataString: String): Manifest[DataSourcePolling] = {
    val value = loadYamlOrThrow[Manifest[DataSourcePolling]](dataString)
    // TODO: Validate the manifest version/kind
    value.copy(content = value.content.withDefaults())
  }
}
