/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import com.typesafe.config.ConfigObject
import pureconfig.error.{
  ConfigReaderException,
  ConfigReaderFailures,
  ConvertFailure,
  KeyNotFound
}

case class RootPollingSource(
  /** Determines where data is sourced from (see [[ExternalSourceKind]]) */
  fetch: ExternalSourceKind,
  /** Defines how raw data is prepared before reading (see [[PrepStepKind]]) */
  prepare: Vector[PrepStepKind] = Vector.empty,
  /** Defines how data is read into structured format (see [[ReaderKind]]) */
  read: ReaderKind,
  /** Pre-processing query that shapes the data (see [[TransformKind]]) */
  preprocess: Option[ConfigObject] = None,
  /** Determines how newly-ingested data should be merged with existing history (see [[MergeStrategyKind]]) */
  merge: MergeStrategyKind,
  /** Collapse partitions of the result to specified number.
    *
    * If zero - the step will be skipped
    */
  coalesce: Int = 1
) extends Resource {

  override def postLoad(): AnyRef = {
    if (preprocess.isDefined && !preprocess.get.containsKey("engine"))
      throw new ConfigReaderException[DerivativeSource](
        ConfigReaderFailures(ConvertFailure(KeyNotFound("engine"), None, ""))
      )
    copy(read = read.postLoad())
  }

  def preprocessEngine: Option[String] = {
    preprocess.map(_.get("engine").unwrapped().asInstanceOf[String])
  }
}
