/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
