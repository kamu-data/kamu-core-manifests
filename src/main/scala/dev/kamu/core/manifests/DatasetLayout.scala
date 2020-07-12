/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.nio.file.Path

/** References a dataset stored in remote volume */
case class DatasetLayout(
  /** Path to dataset metadata directory */
  metadataDir: Path,
  /** Path to directory containing actual data */
  dataDir: Path,
  /** Path to checkpoints directory */
  checkpointsDir: Path,
  /** Stores data that is not essential but can improve performance of operations like data polling */
  cacheDir: Path
)
