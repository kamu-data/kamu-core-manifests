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

sealed trait SourceKind extends Resource {}

object SourceKind {

  /** Root sources are the points of entry of external data into the system.
    * Root source includes information like:
    * - Where to fetch the data from - e.g. source URL, a protocol to use, cache control
    * - How to prepare the binary data - e.g. decompression, file filtering, format conversions
    * - How to interpret the data - e.g. data format, schema to apply, error handling
    * - How to combine data ingested in the past with the new data - e.g. append as log or diff as a snapshot of the current state
    */
  case class Root(
    /** Determines where data is sourced from (see [[FetchSourceKind]]) */
    fetch: FetchSourceKind,
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
  ) extends SourceKind {

    override def postLoad(): AnyRef = {
      if (preprocess.isDefined && !preprocess.get.containsKey("engine"))
        throw new ConfigReaderException[Root](
          ConfigReaderFailures(ConvertFailure(KeyNotFound("engine"), None, ""))
        )
      copy(read = read.postLoad())
    }

    def preprocessEngine: Option[String] = {
      preprocess.map(_.get("engine").unwrapped().asInstanceOf[String])
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /** Derivative sources produce data by transforming and combining one or multiple existing datasets */
  case class Derivative(
    /** Datasets that will be used as sources for this derivative */
    inputs: Vector[Derivative.Input],
    /** Engine-specific processing queries that shape the resulting data (see [[TransformKind]]) */
    transform: ConfigObject
    /** TODO: Output mode (e,g, Spark's Append vs Update)?  */
  ) extends SourceKind {

    override def postLoad(): AnyRef = {
      if (!transform.containsKey("engine"))
        throw new ConfigReaderException[Derivative](
          ConfigReaderFailures(ConvertFailure(KeyNotFound("engine"), None, ""))
        )
      super.postLoad()
    }

    def transformEngine: String = {
      transform.get("engine").unwrapped().asInstanceOf[String]
    }

  }

  object Derivative {
    case class Input(
      /** ID of the input dataset */
      id: DatasetID
      /** TODO: Watermarking configuration? */
    )
  }

}
