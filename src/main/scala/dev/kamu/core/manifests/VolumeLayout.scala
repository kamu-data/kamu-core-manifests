package dev.kamu.core.manifests

import org.apache.hadoop.fs.{FileSystem, Path}
import utils.fs._

case class VolumeLayout(
  /** Directory that contains all definitions of the datasets contained in this volume  */
  datasetsDir: Path,
  /** Directory that contains processing checkpoints */
  checkpointsDir: Path,
  /** Directory that stores the actual data */
  dataDir: Path
) extends Resource[VolumeLayout] {

  def allDirs: Seq[Path] = {
    Seq(datasetsDir, checkpointsDir, dataDir)
  }

  def toAbsolute(fs: FileSystem): VolumeLayout = {
    copy(
      datasetsDir = fs.toAbsolute(datasetsDir),
      checkpointsDir = fs.toAbsolute(checkpointsDir),
      dataDir = fs.toAbsolute(dataDir)
    )
  }
}
