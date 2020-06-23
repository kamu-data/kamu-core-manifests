/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.time.Instant
import spire.math.Interval

/** An individual block in the metadata chain that corresponds to one slice of the output dataset */
case class MetadataBlock(
  /** Hash sum of this metadata block's information */
  blockHash: String = "",
  /** Hash sum of the preceding block */
  prevBlockHash: String,
  /** System time when this block was written */
  systemTime: Instant,
  /** Properties of output data written during this update, if any */
  outputSlice: Option[DataSlice] = None,
  /** Last watermark of the output data stream.
    * Watermarks are usually derived from the event times in data based on the properties of the source,
    * but sometimes they can also be assigned manually. Manual watermarks are useful in cases of slow-moving
    * datasets in order to let the computations continue even when no new events were observed for a long time.
    */
  outputWatermark: Option[Instant] = None,
  //** Describes the output data schema (can be omitted if it doesn't differ from the previous block) */
  // outputDataSchema: Option[Schema],
  /** Defines input data slices used in this block, if any (order corresponds to transform inputs) */
  inputSlices: Vector[DataSlice] = Vector.empty,
  /** Contains the definition of the source of data when it changes */
  source: Option[SourceKind] = None
) extends Resource

case class DataSlice(
  /** Hash sum of the output data slice this block relates to */
  hash: String,
  /** Interval that defines the boundaries of data read or produces within a block */
  interval: Interval[Instant],
  /** Number of records in this slice */
  numRecords: Long
)
