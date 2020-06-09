/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import scala.concurrent.duration.Duration

sealed trait TransformKind extends Resource {

  /** Identifier of the engine that should execute this transform query */
  def engine: String
}

object TransformKind {

  case class SparkSQL(
    engine: String,
    /** Processing steps that shape the data */
    queries: Vector[SparkSQL.Query] = Vector.empty,
    /** Convenience way to provide a single SQL statement with no alias **/
    query: Option[String]
  ) extends TransformKind {

    override def postLoad(): AnyRef = {
      if (query.isDefined)
        copy(queries = Vector(SparkSQL.Query(None, query.get)))
      else
        this
    }

  }

  object SparkSQL {
    case class Query(
      /** An alias given to the result of this step that can be used to referred to it in the later steps.
        * Acts as a shorthand for `CREATE TEMPORARY VIEW <alias> AS (<query>)`.
        */
      alias: Option[String] = None,
      /** An SQL statement **/
      query: String
    )
  }

  case class Flink(
    engine: String,
    /** Event time configuration for input datasets */
    watermarks: Vector[Flink.Watermark] = Vector.empty,
    /** Processing steps that shape the data */
    queries: Vector[Flink.Query] = Vector.empty,
    /** Convenience way to provide a single SQL statement with no alias **/
    query: Option[String]
  ) extends TransformKind {

    override def postLoad(): AnyRef = {
      if (query.isDefined)
        copy(queries = Vector(Flink.Query(None, query.get)))
      else
        this
    }

  }

  object Flink {
    // TODO: Consider making a part of input configuration
    case class Watermark(
      /** ID of the input dataset */
      id: DatasetID,
      /** Name of the column to be used as event time */
      eventTimeColumn: String,
      /** Duration by which events can be late to still be considered */
      maxLateBy: Option[Duration],
      /** When specified, the stream will be treated as a temporal table.
        * See: https://ci.apache.org/projects/flink/flink-docs-stable/dev/table/streaming/temporal_tables.html
        */
      primaryKey: Vector[String] = Vector.empty
    )

    case class Query(
      /** An alias given to the result of this step that can be used to referred to it in the later steps.
        * Acts as a shorthand for `CREATE TEMPORARY VIEW <alias> AS (<query>)`.
        */
      alias: Option[String] = None,
      /** An SQL statement **/
      query: String
    )
  }

}
