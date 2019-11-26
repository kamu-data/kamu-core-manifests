/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI

import org.apache.hadoop.fs.Path

//////////////////////////////////////////////////////////////////////////////

sealed trait ExternalSourceKind

object ExternalSourceKind {

  case class FetchUrl(
    /** Data source location */
    url: URI,
    /** Caching behavior settings */
    cache: CachingKind = CachingKind.Default()
  ) extends ExternalSourceKind

  case class FetchFilesGlob(
    /** Glob for data source files */
    path: Path,
    /** Caching behavior for the individual files */
    cache: CachingKind = CachingKind.Default()
  ) extends ExternalSourceKind

}

//////////////////////////////////////////////////////////////////////////////

sealed trait CachingKind

object CachingKind {
  case class Default() extends CachingKind

  case class Forever() extends CachingKind
}
