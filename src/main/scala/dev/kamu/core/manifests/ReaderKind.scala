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

  }

  //////////////////////////////////////////////////////////////////////////////

  case class Csv(
    /** Sets a single character as a separator for each field and value */
    sep: Option[String] = None,
    /** Decodes the CSV files by the given encoding type */
    encoding: Option[String] = None,
    /** Sets a single character used for escaping quoted values where the separator can be part of the value.
      * Set an empty string to turn off quotations. */
    quote: Option[String] = None,
    /** Sets a single character used for escaping quotes inside an already quoted value */
    escape: Option[String] = None,
    /** Sets a single character used for skipping lines beginning with this character */
    comment: Option[String] = None,
    /** Use the first line as names of columns */
    header: Option[Boolean] = None,
    /** If it is set to true, the specified or inferred schema will be forcibly applied to datasource files,
      * and headers in CSV files will be ignored. If the option is set to false, the schema will be validated against
      * all headers in CSV files in the case when the header option is set to true. */
    enforceSchema: Option[Boolean] = None,
    /** Infers the input schema automatically from data. It requires one extra pass over the data. */
    inferSchema: Option[Boolean] = None,
    /** A flag indicating whether or not leading whitespaces from values being read should be skipped */
    ignoreLeadingWhiteSpace: Option[Boolean] = None,
    /** A flag indicating whether or not trailing whitespaces from values being read should be skipped */
    ignoreTrailingWhiteSpace: Option[Boolean] = None,
    /** Sets the string representation of a null value */
    nullValue: Option[String] = None,
    /** Sets the string representation of an empty value */
    emptyValue: Option[String] = None,
    /** Sets the string representation of a non-number value */
    nanValue: Option[String] = None,
    /** Sets the string representation of a positive infinity value */
    positiveInf: Option[String] = None,
    /** Sets the string representation of a negative infinity value */
    negativeInf: Option[String] = None,
    /** Sets the string that indicates a date format */
    dateFormat: Option[String] = None,
    /** Sets the string that indicates a timestamp format */
    timestampFormat: Option[String] = None,
    /** Parse one record, which may span multiple lines */
    multiLine: Option[Boolean] = None,
    /** A DDL-formatted schema */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind {
    override def asGeneric(): ReaderKind = {
      Generic(
        name = "csv",
        options = Map(
          "sep" -> sep,
          "encoding" -> encoding,
          "quote" -> quote,
          "escape" -> escape,
          "comment" -> comment,
          "header" -> header,
          "enforceSchema" -> enforceSchema.orElse(Some(false)),
          "inferSchema" -> inferSchema,
          "ignoreLeadingWhiteSpace" -> ignoreLeadingWhiteSpace,
          "ignoreTrailingWhiteSpace" -> ignoreTrailingWhiteSpace,
          "nullValue" -> nullValue,
          "emptyValue" -> emptyValue,
          "nanValue" -> nanValue,
          "positiveInf" -> positiveInf,
          "negativeInf" -> negativeInf,
          "dateFormat" -> dateFormat,
          "timestampFormat" -> timestampFormat,
          "multiLine" -> multiLine
        ).collect({
          case (k, Some(s: String))  => k -> s
          case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
        }),
        schema = schema
      )
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  case class Json(
    /** Sets the string that indicates a date format */
    dateFormat: Option[String],
    /** Allows to forcibly set one of standard basic or extended encoding */
    encoding: Option[String],
    /** Parse one record, which may span multiple lines, per file */
    multiLine: Option[Boolean],
    /** Infers all primitive values as a string type */
    primitivesAsString: Option[Boolean],
    /** Sets the string that indicates a timestamp format */
    timestampFormat: Option[String],
    /** A DDL-formatted schema */
    schema: Vector[String] = Vector.empty
  ) extends ReaderKind {
    override def asGeneric(): ReaderKind = {
      Generic(
        name = "json",
        options = Map(
          "dateFormat" -> dateFormat,
          "encoding" -> encoding,
          "multiLine" -> multiLine,
          "primitivesAsString" -> primitivesAsString,
          "timestampFormat" -> timestampFormat
        ).collect({
          case (k, Some(s: String))  => k -> s
          case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
        }),
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
