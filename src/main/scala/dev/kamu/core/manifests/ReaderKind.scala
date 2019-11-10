package dev.kamu.core.manifests

//////////////////////////////////////////////////////////////////////////////

sealed trait ReaderKind {
  def schema: Vector[String]
  def postLoad(): ReaderKind = { this }
  def asGeneric(): ReaderKind = {
    throw new RuntimeException("Not a generic reader")
  }
}

//////////////////////////////////////////////////////////////////////////////
// Generic
//////////////////////////////////////////////////////////////////////////////

case class ReaderGeneric(
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
    copy(options = ReaderGeneric.DEFAULT_READER_OPTIONS ++ options)
  }

}

object ReaderGeneric {
  val DEFAULT_READER_OPTIONS: Map[String, String] = Map(
    "mode" -> "FAILFAST"
  )
}

//////////////////////////////////////////////////////////////////////////////

case class ReaderCsv(
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
  /** A DDL-formatted schema.
    *
    * Schema can be used to coerce values into more appropriate data types.
    */
  schema: Vector[String] = Vector.empty
) extends ReaderKind {
  override def asGeneric(): ReaderKind = {
    ReaderGeneric(
      name = "csv",
      options = ReaderGeneric.DEFAULT_READER_OPTIONS ++ Map(
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
// Special
//////////////////////////////////////////////////////////////////////////////

case class ReaderGeojson(
  /** A DDL-formatted schema.
    *
    * Schema can be used to coerce values into more appropriate data types.
    */
  schema: Vector[String] = Vector.empty
) extends ReaderKind

case class ReaderShapefile(
  /** Regex for finding desired data set within shapefile's zip archive */
  subPathRegex: Option[String] = None,
  /** A DDL-formatted schema.
    *
    * Schema can be used to coerce values into more appropriate data types.
    */
  schema: Vector[String] = Vector.empty
) extends ReaderKind