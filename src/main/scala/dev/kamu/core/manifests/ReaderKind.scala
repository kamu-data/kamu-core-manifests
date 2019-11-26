/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

//////////////////////////////////////////////////////////////////////////////

sealed trait ReaderKind {
  def schema: Vector[String]
  def postLoad(): ReaderKind = { this }
  def asGeneric(): ReaderKind = {
    throw new RuntimeException("Not a generic reader")
  }
}

object ReaderKind {

  //////////////////////////////////////////////////////////////////////////////
  // Generic
  //////////////////////////////////////////////////////////////////////////////

  case class Generic(
    /** A raw data format (as supported by Spark's read function) */
    name: String,
    /** Options to pass into the [[org.apache.spark.sql.DataFrameReader]] */
    options: Map[String, String] = Map.empty,
    /** A DDL-formatted schema.
      *
      * Schema can be used to coerce values into more appropriate data types.
      */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind {

    override def asGeneric(): ReaderKind = this

    override def postLoad(): ReaderKind = {
      copy(options = Generic.DEFAULT_READER_OPTIONS ++ options)
    }

  }

  object Generic {
    val DEFAULT_READER_OPTIONS: Map[String, String] = Map(
      "mode" -> "FAILFAST"
    )
  }

  //////////////////////////////////////////////////////////////////////////////

  case class Csv(
    charset: String = "UTF-8",
    comment: String = "#",
    dateFormat: String = "",
    delimiter: String = ",",
    escape: String = "\\",
    header: Boolean = false,
    inferSchema: Boolean = false,
    multiline: Boolean = false,
    nullValue: String = "",
    quote: String = "\"",
    /** A DDL-formatted schema */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind {
    override def asGeneric(): ReaderKind = {
      Generic(
        name = "csv",
        options = Generic.DEFAULT_READER_OPTIONS ++ Map(
          "charset" -> charset,
          "comment" -> comment,
          "delimiter" -> delimiter,
          "escape" -> escape,
          "header" -> (if (header) "true" else "false"),
          "inferSchema" -> (if (inferSchema) "true" else "false"),
          "multiline" -> (if (multiline) "true" else "false"),
          "nullValue" -> nullValue,
          "quote" -> quote
        ) ++ (
          if (dateFormat.nonEmpty) Map("dateFormat" -> dateFormat)
          else Map.empty
        ),
        schema = schema
      )
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  case class Json(
    /** Sets the string that indicates a date format */
    dateFormat: String = "",
    /** Allows to forcibly set one of standard basic or extended encoding */
    encoding: String = "",
    /** Parse one record, which may span multiple lines, per file */
    multiline: Boolean = false,
    /** Infers all primitive values as a string type */
    primitivesAsString: Boolean = false,
    /** Sets the string that indicates a timestamp format */
    timestampFormat: String = "",
    /** A DDL-formatted schema */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind {
    override def asGeneric(): ReaderKind = {
      Generic(
        name = "json",
        options = Generic.DEFAULT_READER_OPTIONS ++ Map(
          "multiLine" -> (if (multiline) "true" else "false"),
          "primitivesAsString" -> (if (primitivesAsString) "true" else "false")
        ) ++ (
          if (dateFormat.nonEmpty) Map("dateFormat" -> dateFormat)
          else Map.empty
        ) ++ (
          if (encoding.nonEmpty) Map("encoding" -> encoding)
          else Map.empty
        ) ++ (
          if (timestampFormat.nonEmpty)
            Map("timestampFormat" -> timestampFormat)
          else Map.empty
        ),
        schema = schema
      )
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Special
  //////////////////////////////////////////////////////////////////////////////

  case class Geojson(
    /** A DDL-formatted schema.
      *
      * Schema can be used to coerce values into more appropriate data types.
      */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind

  case class Shapefile(
    /** Regex for finding desired data set within shapefile's zip archive */
    subPathRegex: Option[String] = None,
    /** A DDL-formatted schema.
      *
      * Schema can be used to coerce values into more appropriate data types.
      */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind

}
