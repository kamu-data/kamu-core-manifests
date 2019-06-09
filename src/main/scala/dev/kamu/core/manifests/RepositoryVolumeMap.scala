package dev.kamu.core.manifests

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
) extends Resource[RepositoryVolumeMap]
