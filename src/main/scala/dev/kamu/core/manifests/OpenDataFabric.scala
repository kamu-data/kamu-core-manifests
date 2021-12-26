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

///////////////////////////////////////////////////////////////////////////////
// WARNING: This file is auto-generated from Open Data Fabric Schemas
// See: http://opendatafabric.org/
///////////////////////////////////////////////////////////////////////////////

case class Multihash(s: String) extends AnyVal {
  override def toString: String = s
}

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}

case class DatasetName(s: String) extends AnyVal {
  override def toString: String = s
}

////////////////////////////////////////////////////////////////////////////////
// BlockInterval
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#blockinterval-schema
////////////////////////////////////////////////////////////////////////////////

case class BlockInterval(
  start: Multihash,
  end: Multihash
)

////////////////////////////////////////////////////////////////////////////////
// DatasetSnapshot
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetsnapshot-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetSnapshot(
  name: DatasetName,
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
    inputs: Vector[TransformInput],
    transform: Transform
  ) extends DatasetSource
}

////////////////////////////////////////////////////////////////////////////////
// DatasetVocabulary
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetvocabulary-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetVocabulary(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None,
  offsetColumn: Option[String] = None
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
// ExecuteQueryInput
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executequeryinput-schema
////////////////////////////////////////////////////////////////////////////////

case class ExecuteQueryInput(
  datasetID: DatasetID,
  datasetName: DatasetName,
  vocab: DatasetVocabulary,
  dataInterval: Option[OffsetInterval] = None,
  dataPaths: Vector[Path],
  schemaFile: Path,
  explicitWatermarks: Vector[Watermark]
)

////////////////////////////////////////////////////////////////////////////////
// ExecuteQueryRequest
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executequeryrequest-schema
////////////////////////////////////////////////////////////////////////////////

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  datasetName: DatasetName,
  systemTime: Instant,
  offset: Long,
  vocab: DatasetVocabulary,
  transform: Transform,
  inputs: Vector[ExecuteQueryInput],
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
    dataInterval: Option[OffsetInterval] = None,
    outputWatermark: Option[Instant] = None
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
// InputSlice
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#inputslice-schema
////////////////////////////////////////////////////////////////////////////////

case class InputSlice(
  datasetID: DatasetID,
  blockInterval: Option[BlockInterval] = None,
  dataInterval: Option[OffsetInterval] = None
)

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
  prevBlockHash: Option[Multihash] = None,
  systemTime: Instant,
  outputSlice: Option[OutputSlice] = None,
  outputWatermark: Option[Instant] = None,
  inputSlices: Option[Vector[InputSlice]] = None,
  source: Option[DatasetSource] = None,
  vocab: Option[DatasetVocabulary] = None,
  seed: Option[DatasetID] = None
)

////////////////////////////////////////////////////////////////////////////////
// OffsetInterval
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#offsetinterval-schema
////////////////////////////////////////////////////////////////////////////////

case class OffsetInterval(
  start: Long,
  end: Long
)

////////////////////////////////////////////////////////////////////////////////
// OutputSlice
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#outputslice-schema
////////////////////////////////////////////////////////////////////////////////

case class OutputSlice(
  dataLogicalHash: Multihash,
  dataPhysicalHash: Multihash,
  dataInterval: OffsetInterval
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
// TransformInput
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transforminput-schema
////////////////////////////////////////////////////////////////////////////////

case class TransformInput(
  id: Option[DatasetID] = None,
  name: DatasetName
)

////////////////////////////////////////////////////////////////////////////////
// Watermark
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#watermark-schema
////////////////////////////////////////////////////////////////////////////////

case class Watermark(
  systemTime: Instant,
  eventTime: Instant
)
