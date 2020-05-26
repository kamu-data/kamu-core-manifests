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

sealed trait FetchSourceKind {

  /** Where the event time should be taken from */
  def eventTime: Option[EventTimeKind]

  /** Caching behavior for the individual files */
  def cache: Option[CachingKind]
}

object FetchSourceKind {

  case class Url(
    /** Data source location */
    url: URI,
    eventTime: Option[EventTimeKind] = None,
    cache: Option[CachingKind] = None
  ) extends FetchSourceKind

  case class FilesGlob(
    /** Glob for data source files */
    path: Path,
    orderBy: Option[OrderingKind] = None,
    eventTime: Option[EventTimeKind] = None,
    cache: Option[CachingKind] = None
  ) extends FetchSourceKind

}

//////////////////////////////////////////////////////////////////////////////

sealed trait EventTimeKind

object EventTimeKind {

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

  /** After source was processed once it will never be ingested again */
  case class Forever() extends CachingKind

}

//////////////////////////////////////////////////////////////////////////////

sealed trait OrderingKind

object OrderingKind {

  /** Use event time extracted from metadata for ordering, see [[EventTimeKind]] */
  case class ByMetadataEventTime() extends OrderingKind

  /** Use alphabetical order */
  case class ByName() extends OrderingKind

}
