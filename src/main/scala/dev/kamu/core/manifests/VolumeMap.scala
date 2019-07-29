package dev.kamu.core.manifests

import org.apache.hadoop.fs.{FileSystem, Path}
import utils.fs._

/** Describes the layout of the data repository on disk */
case class VolumeMap(
  /** Directory to store downloaded data in before processing */
  downloadDir: Path,
  /** Directory to store cache information in */
  checkpointDir: Path,
  /** Root data set directory for ingested data */
  dataDirRoot: Path,
  /** Data set directory for derivative data */
  dataDirDeriv: Path
) extends Resource[VolumeMap] {

  def allPaths: Seq[Path] = Seq(
    downloadDir,
    checkpointDir,
    dataDirRoot,
    dataDirDeriv
  )

  def toAbsolute(fs: FileSystem): VolumeMap = {
    copy(
      downloadDir = fs.toAbsolute(downloadDir),
      checkpointDir = fs.toAbsolute(checkpointDir),
      dataDirRoot = fs.toAbsolute(dataDirRoot),
      dataDirDeriv = fs.toAbsolute(dataDirDeriv)
    )
  }

}
