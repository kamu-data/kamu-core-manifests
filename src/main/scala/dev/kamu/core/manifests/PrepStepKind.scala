package dev.kamu.core.manifests

import org.apache.hadoop.fs.Path

sealed trait PrepStepKind

case class PrepStepDecompress(
  /** Name of a compression algorithm used on data */
  format: String,
  /** Path to a data file within a multi-file archive */
  subPath: Option[Path] = None,
  /** Regex for finding desired data file within a multi-file archive */
  subPathRegex: Option[String] = None
) extends PrepStepKind
