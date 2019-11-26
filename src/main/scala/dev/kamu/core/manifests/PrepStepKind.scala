/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import org.apache.hadoop.fs.Path

sealed trait PrepStepKind

object PrepStepKind {

  /** Decompresses the archive file */
  case class Decompress(
    /** Name of a compression algorithm used on data */
    format: String,
    /** Path to a data file within a multi-file archive */
    subPath: Option[Path] = None,
    /** Regex for finding desired data file within a multi-file archive */
    subPathRegex: Option[String] = None
  ) extends PrepStepKind

  /** Executes external command to process the data using piped input/output */
  case class Pipe(
    /** Command to execute and its arguments */
    command: Vector[String]
  ) extends PrepStepKind

}
