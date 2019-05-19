package dev.kamu.core.manifests;

import java.net.URI
import org.apache.hadoop.fs.Path

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
  readerOptions: Map[String, String] = Map(
    "mode" -> "FAILFAST"
  ),
  /** A DDL-formatted schema.
    *
    * Schema can be used to coerce values into more appropriate data types.
    */
  schema: Vector[String] = Vector.empty,
  /** Pre-processing steps to shape the data */
  preprocess: Vector[ProcessingStepSQL] = Vector.empty,
  /** One of the supported merge strategies (see [[MergeStrategy]]) */
  mergeStrategy: MergeStrategy = Append(),
  /** Collapse partitions of the result to specified number.
    *
    * If zero - the step will be skipped
    */
  coalesce: Int = 1
)
