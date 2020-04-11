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

sealed trait FetchKind {

  /** Where the event time should be taken from */
  def eventTime: Option[EventTimeKind]

  /** Caching behavior for the individual files */
  def cache: Option[CachingKind]
}

object FetchKind {

  case class FetchUrl(
    /** Data source location */
    url: URI,
    eventTime: Option[EventTimeKind] = None,
    cache: Option[CachingKind] = None
  ) extends FetchKind

  case class FetchFilesGlob(
    /** Glob for data source files */
    path: Path,
    eventTime: Option[EventTimeKind] = None,
    cache: Option[CachingKind] = None
  ) extends FetchKind

}

//////////////////////////////////////////////////////////////////////////////

sealed trait EventTimeKind

object EventTimeKind {

  case class FromSystemTime(
    ) extends EventTimeKind

  case class FromPath(
    /** Regular expression where first group contains the timestamp string */
    pattern: String,
    /** Format of the expected timestamp in java.text.SimpleDateFormat form */
    timestampFormat: String
  ) extends EventTimeKind

}

//////////////////////////////////////////////////////////////////////////////

sealed trait CachingKind

object CachingKind {

  case class Forever() extends CachingKind

}
