/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.nio.file.Path

import dev.kamu.core.utils.fs._

case class VolumeLayout(
  /** Directory that contains metadata of all datasets contained in this volume  */
  metadataDir: Path,
  /** Directory that contains processing checkpoints */
  checkpointsDir: Path,
  /** Directory that stores the actual data */
  dataDir: Path,
  /** Stores data that is not essential but can improve performance of operations like data polling */
  cacheDir: Path
) extends Resource {

  def allDirs: Seq[Path] = {
    Seq(metadataDir, checkpointsDir, dataDir, cacheDir)
  }

  def relativeTo(path: Path): VolumeLayout = {
    copy(
      metadataDir = path / metadataDir,
      checkpointsDir = path / checkpointsDir,
      dataDir = path / dataDir,
      cacheDir = path / cacheDir
    )
  }

  def toAbsolute: VolumeLayout = {
    copy(
      metadataDir = metadataDir.toAbsolutePath,
      checkpointsDir = checkpointsDir.toAbsolutePath,
      dataDir = dataDir.toAbsolutePath,
      cacheDir = cacheDir.toAbsolutePath
    )
  }
}
