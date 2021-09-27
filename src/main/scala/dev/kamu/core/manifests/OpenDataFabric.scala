/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI
import java.nio.file.Path
import java.time.Instant

import com.typesafe.config.ConfigObject
import spire.math.Interval

////////////////////////////////////////////////////////////////////////////////
// WARNING: This file is auto-generated from Open Data Fabric Schemas
// See: http://opendatafabric.org/
////////////////////////////////////////////////////////////////////////////////

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}

////////////////////////////////////////////////////////////////////////////////
// DataSlice
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#dataslice-schema
////////////////////////////////////////////////////////////////////////////////

case class DataSlice(
  hash: String,
  interval: Interval[Instant],
  numRecords: Long
)

////////////////////////////////////////////////////////////////////////////////
// DatasetSnapshot
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetsnapshot-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetSnapshot(
  id: DatasetID,
  source: DatasetSource,
  vocab: Option[DatasetVocabulary] = None
)

////////////////////////////////////////////////////////////////////////////////
// DatasetSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetsource-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait DatasetSource

object DatasetSource {
  case class Root(
    fetch: FetchStep,
    prepare: Option[Vector[PrepStep]] = None,
    read: ReadStep,
    preprocess: Option[Transform] = None,
    merge: MergeStrategy
  ) extends DatasetSource

  case class Derivative(
    inputs: Vector[DatasetID],
    transform: Transform
  ) extends DatasetSource
}

////////////////////////////////////////////////////////////////////////////////
// DatasetVocabulary
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetvocabulary-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetVocabulary(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None
)

////////////////////////////////////////////////////////////////////////////////
// EventTimeSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#eventtimesource-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait EventTimeSource

object EventTimeSource {
  case class FromMetadata(
    ) extends EventTimeSource

  case class FromPath(
    pattern: String,
    timestampFormat: Option[String] = None
  ) extends EventTimeSource
}

////////////////////////////////////////////////////////////////////////////////
// ExecuteQueryRequest
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executequeryrequest-schema
////////////////////////////////////////////////////////////////////////////////

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  vocab: DatasetVocabulary,
  transform: Transform,
  inputs: Vector[QueryInput],
  prevCheckpointDir: Option[Path] = None,
  newCheckpointDir: Path,
  outDataPath: Path
)

////////////////////////////////////////////////////////////////////////////////
// ExecuteQueryResponse
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executequeryresponse-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait ExecuteQueryResponse

object ExecuteQueryResponse {
  case class Progress(
    ) extends ExecuteQueryResponse

  case class Success(
    metadataBlock: MetadataBlock
  ) extends ExecuteQueryResponse

  case class InvalidQuery(
    message: String
  ) extends ExecuteQueryResponse

  case class InternalError(
    message: String,
    backtrace: Option[String] = None
  ) extends ExecuteQueryResponse
}

////////////////////////////////////////////////////////////////////////////////
// FetchStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#fetchstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait FetchStep

object FetchStep {
  case class Url(
    url: URI,
    eventTime: Option[EventTimeSource] = None,
    cache: Option[SourceCaching] = None
  ) extends FetchStep

  case class FilesGlob(
    path: String,
    eventTime: Option[EventTimeSource] = None,
    cache: Option[SourceCaching] = None,
    order: Option[SourceOrdering] = None
  ) extends FetchStep
}

sealed trait SourceOrdering

object SourceOrdering {
  case object ByEventTime extends SourceOrdering
  case object ByName extends SourceOrdering
}

////////////////////////////////////////////////////////////////////////////////
// MergeStrategy
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#mergestrategy-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait MergeStrategy

object MergeStrategy {
  case class Append(
    ) extends MergeStrategy

  case class Ledger(
    primaryKey: Vector[String]
  ) extends MergeStrategy

  case class Snapshot(
    primaryKey: Vector[String],
    compareColumns: Option[Vector[String]] = None,
    observationColumn: Option[String] = None,
    obsvAdded: Option[String] = None,
    obsvChanged: Option[String] = None,
    obsvRemoved: Option[String] = None
  ) extends MergeStrategy
}

////////////////////////////////////////////////////////////////////////////////
// MetadataBlock
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#metadatablock-schema
////////////////////////////////////////////////////////////////////////////////

case class MetadataBlock(
  blockHash: String,
  prevBlockHash: Option[String] = None,
  systemTime: Instant,
  outputSlice: Option[DataSlice] = None,
  outputWatermark: Option[Instant] = None,
  inputSlices: Option[Vector[DataSlice]] = None,
  source: Option[DatasetSource] = None,
  vocab: Option[DatasetVocabulary] = None
)

////////////////////////////////////////////////////////////////////////////////
// PrepStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#prepstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait PrepStep

object PrepStep {
  case class Decompress(
    format: CompressionFormat,
    subPath: Option[String] = None
  ) extends PrepStep

  case class Pipe(
    command: Vector[String]
  ) extends PrepStep
}

sealed trait CompressionFormat

object CompressionFormat {
  case object Gzip extends CompressionFormat
  case object Zip extends CompressionFormat
}

////////////////////////////////////////////////////////////////////////////////
// QueryInput
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#queryinput-schema
////////////////////////////////////////////////////////////////////////////////

case class QueryInput(
  datasetID: DatasetID,
  vocab: DatasetVocabulary,
  interval: Interval[Instant],
  dataPaths: Vector[Path],
  schemaFile: Path,
  explicitWatermarks: Vector[Watermark]
)

////////////////////////////////////////////////////////////////////////////////
// ReadStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#readstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait ReadStep {
  def schema: Option[Vector[String]]
}

object ReadStep {
  case class Csv(
    schema: Option[Vector[String]] = None,
    separator: Option[String] = None,
    encoding: Option[String] = None,
    quote: Option[String] = None,
    escape: Option[String] = None,
    comment: Option[String] = None,
    header: Option[Boolean] = None,
    enforceSchema: Option[Boolean] = None,
    inferSchema: Option[Boolean] = None,
    ignoreLeadingWhiteSpace: Option[Boolean] = None,
    ignoreTrailingWhiteSpace: Option[Boolean] = None,
    nullValue: Option[String] = None,
    emptyValue: Option[String] = None,
    nanValue: Option[String] = None,
    positiveInf: Option[String] = None,
    negativeInf: Option[String] = None,
    dateFormat: Option[String] = None,
    timestampFormat: Option[String] = None,
    multiLine: Option[Boolean] = None
  ) extends ReadStep

  case class JsonLines(
    schema: Option[Vector[String]] = None,
    dateFormat: Option[String] = None,
    encoding: Option[String] = None,
    multiLine: Option[Boolean] = None,
    primitivesAsString: Option[Boolean] = None,
    timestampFormat: Option[String] = None
  ) extends ReadStep

  case class GeoJson(
    schema: Option[Vector[String]] = None
  ) extends ReadStep

  case class EsriShapefile(
    schema: Option[Vector[String]] = None,
    subPath: Option[String] = None
  ) extends ReadStep
}

////////////////////////////////////////////////////////////////////////////////
// SourceCaching
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#sourcecaching-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait SourceCaching

object SourceCaching {
  case class Forever(
    ) extends SourceCaching
}

////////////////////////////////////////////////////////////////////////////////
// SqlQueryStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#sqlquerystep-schema
////////////////////////////////////////////////////////////////////////////////

case class SqlQueryStep(
  alias: Option[String] = None,
  query: String
)

////////////////////////////////////////////////////////////////////////////////
// TemporalTable
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#temporaltable-schema
////////////////////////////////////////////////////////////////////////////////

case class TemporalTable(
  id: String,
  primaryKey: Vector[String]
)

////////////////////////////////////////////////////////////////////////////////
// Transform
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transform-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait Transform {
  def engine: String
  def version: Option[String]
}

object Transform {
  case class Sql(
    engine: String,
    version: Option[String] = None,
    query: Option[String] = None,
    queries: Option[Vector[SqlQueryStep]] = None,
    temporalTables: Option[Vector[TemporalTable]] = None
  ) extends Transform
}

////////////////////////////////////////////////////////////////////////////////
// Watermark
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#watermark-schema
////////////////////////////////////////////////////////////////////////////////

case class Watermark(
  systemTime: Instant,
  eventTime: Instant
)
