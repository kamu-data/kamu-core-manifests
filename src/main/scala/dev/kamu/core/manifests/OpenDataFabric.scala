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

case class DatasetId(s: String) extends AnyVal {
  override def toString: String = s
  def toMultibase(): String = {
    assert(s.startsWith("did:odf:"))
    s.substring(8, s.length)
  }
}

case class DatasetName(s: String) extends AnyVal {
  override def toString: String = s
}

case class DatasetAlias(s: String) extends AnyVal {
  override def toString: String = s
}

case class DatasetRef(s: String) extends AnyVal {
  override def toString: String = s
}

case class DatasetRefAny(s: String) extends AnyVal {
  override def toString: String = s
}

////////////////////////////////////////////////////////////////////////////////
// AddData
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#adddata-schema
////////////////////////////////////////////////////////////////////////////////

case class AddData(
  prevCheckpoint: Option[Multihash] = None,
  prevOffset: Option[Long] = None,
  newData: Option[DataSlice] = None,
  newCheckpoint: Option[Checkpoint] = None,
  newWatermark: Option[Instant] = None,
  newSourceState: Option[SourceState] = None
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// AddPushSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#addpushsource-schema
////////////////////////////////////////////////////////////////////////////////

case class AddPushSource(
  sourceName: String,
  read: ReadStep,
  preprocess: Option[Transform] = None,
  merge: MergeStrategy
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// AttachmentEmbedded
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#attachmentembedded-schema
////////////////////////////////////////////////////////////////////////////////

case class AttachmentEmbedded(
  path: String,
  content: String
)

////////////////////////////////////////////////////////////////////////////////
// Attachments
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#attachments-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait Attachments

object Attachments {
  case class Embedded(
    items: Vector[AttachmentEmbedded]
  ) extends Attachments
}

////////////////////////////////////////////////////////////////////////////////
// Checkpoint
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#checkpoint-schema
////////////////////////////////////////////////////////////////////////////////

case class Checkpoint(
  physicalHash: Multihash,
  size: Long
)

////////////////////////////////////////////////////////////////////////////////
// DataSlice
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#dataslice-schema
////////////////////////////////////////////////////////////////////////////////

case class DataSlice(
  logicalHash: Multihash,
  physicalHash: Multihash,
  offsetInterval: OffsetInterval,
  size: Long
)

////////////////////////////////////////////////////////////////////////////////
// DatasetKind
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetkind-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait DatasetKind
object DatasetKind {
  case object Root extends DatasetKind
  case object Derivative extends DatasetKind
  case object Remote extends DatasetKind
}

////////////////////////////////////////////////////////////////////////////////
// DatasetSnapshot
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetsnapshot-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetSnapshot(
  name: DatasetAlias,
  kind: DatasetKind,
  metadata: Vector[MetadataEvent]
)

////////////////////////////////////////////////////////////////////////////////
// DatasetVocabulary
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#datasetvocabulary-schema
////////////////////////////////////////////////////////////////////////////////

case class DatasetVocabulary(
  systemTimeColumn: String,
  eventTimeColumn: String,
  offsetColumn: String
)

object DatasetVocabulary {
  def default(): DatasetVocabulary = {
    DatasetVocabulary(
      systemTimeColumn = "system_time",
      eventTimeColumn = "event_time",
      offsetColumn = "offset"
    )
  }
}

////////////////////////////////////////////////////////////////////////////////
// DisablePollingSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#disablepollingsource-schema
////////////////////////////////////////////////////////////////////////////////

case class DisablePollingSource(
  ) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// DisablePushSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#disablepushsource-schema
////////////////////////////////////////////////////////////////////////////////

case class DisablePushSource(
  sourceName: String
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// EnvVar
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#envvar-schema
////////////////////////////////////////////////////////////////////////////////

case class EnvVar(
  name: String,
  value: Option[String] = None
)

////////////////////////////////////////////////////////////////////////////////
// EventTimeSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#eventtimesource-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait EventTimeSource

object EventTimeSource {
  case class FromMetadata(
    ) extends EventTimeSource

  case class FromSystemTime(
    ) extends EventTimeSource

  case class FromPath(
    pattern: String,
    timestampFormat: Option[String] = None
  ) extends EventTimeSource
}

////////////////////////////////////////////////////////////////////////////////
// ExecuteTransform
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executetransform-schema
////////////////////////////////////////////////////////////////////////////////

case class ExecuteTransform(
  queryInputs: Vector[ExecuteTransformInput],
  prevCheckpoint: Option[Multihash] = None,
  prevOffset: Option[Long] = None,
  newData: Option[DataSlice] = None,
  newCheckpoint: Option[Checkpoint] = None,
  newWatermark: Option[Instant] = None
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// ExecuteTransformInput
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#executetransforminput-schema
////////////////////////////////////////////////////////////////////////////////

case class ExecuteTransformInput(
  datasetId: DatasetId,
  prevBlockHash: Option[Multihash] = None,
  newBlockHash: Option[Multihash] = None,
  prevOffset: Option[Long] = None,
  newOffset: Option[Long] = None
)

////////////////////////////////////////////////////////////////////////////////
// FetchStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#fetchstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait FetchStep

object FetchStep {
  case class Url(
    url: URI,
    eventTime: Option[EventTimeSource] = None,
    cache: Option[SourceCaching] = None,
    headers: Option[Vector[RequestHeader]] = None
  ) extends FetchStep

  case class FilesGlob(
    path: String,
    eventTime: Option[EventTimeSource] = None,
    cache: Option[SourceCaching] = None,
    order: Option[SourceOrdering] = None
  ) extends FetchStep

  case class Container(
    image: String,
    command: Option[Vector[String]] = None,
    args: Option[Vector[String]] = None,
    env: Option[Vector[EnvVar]] = None
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
  systemTime: Instant,
  prevBlockHash: Option[Multihash] = None,
  sequenceNumber: Long,
  event: MetadataEvent
)

////////////////////////////////////////////////////////////////////////////////
// MetadataEvent
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#metadataevent-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait MetadataEvent

object MetadataEvent {}

////////////////////////////////////////////////////////////////////////////////
// OffsetInterval
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#offsetinterval-schema
////////////////////////////////////////////////////////////////////////////////

case class OffsetInterval(
  start: Long,
  end: Long
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
// RawQueryRequest
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#rawqueryrequest-schema
////////////////////////////////////////////////////////////////////////////////

case class RawQueryRequest(
  inputDataPaths: Vector[Path],
  transform: Transform,
  outputDataPath: Path
)

////////////////////////////////////////////////////////////////////////////////
// RawQueryResponse
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#rawqueryresponse-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait RawQueryResponse

object RawQueryResponse {
  case class Progress(
    ) extends RawQueryResponse

  case class Success(
    numRecords: Long
  ) extends RawQueryResponse

  case class InvalidQuery(
    message: String
  ) extends RawQueryResponse

  case class InternalError(
    message: String,
    backtrace: Option[String] = None
  ) extends RawQueryResponse
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
    header: Option[Boolean] = None,
    inferSchema: Option[Boolean] = None,
    nullValue: Option[String] = None,
    dateFormat: Option[String] = None,
    timestampFormat: Option[String] = None
  ) extends ReadStep

  case class Json(
    subPath: Option[String] = None,
    schema: Option[Vector[String]] = None,
    dateFormat: Option[String] = None,
    encoding: Option[String] = None,
    timestampFormat: Option[String] = None
  ) extends ReadStep

  case class NdJson(
    schema: Option[Vector[String]] = None,
    dateFormat: Option[String] = None,
    encoding: Option[String] = None,
    timestampFormat: Option[String] = None
  ) extends ReadStep

  case class GeoJson(
    schema: Option[Vector[String]] = None
  ) extends ReadStep

  case class NdGeoJson(
    schema: Option[Vector[String]] = None
  ) extends ReadStep

  case class EsriShapefile(
    schema: Option[Vector[String]] = None,
    subPath: Option[String] = None
  ) extends ReadStep

  case class Parquet(
    schema: Option[Vector[String]] = None
  ) extends ReadStep
}

////////////////////////////////////////////////////////////////////////////////
// RequestHeader
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#requestheader-schema
////////////////////////////////////////////////////////////////////////////////

case class RequestHeader(
  name: String,
  value: String
)

////////////////////////////////////////////////////////////////////////////////
// Seed
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#seed-schema
////////////////////////////////////////////////////////////////////////////////

case class Seed(
  datasetId: DatasetId,
  datasetKind: DatasetKind
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetAttachments
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setattachments-schema
////////////////////////////////////////////////////////////////////////////////

case class SetAttachments(
  attachments: Attachments
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetDataSchema
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setdataschema-schema
////////////////////////////////////////////////////////////////////////////////

case class SetDataSchema(
  schema: Array[Byte]
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetInfo
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setinfo-schema
////////////////////////////////////////////////////////////////////////////////

case class SetInfo(
  description: Option[String] = None,
  keywords: Option[Vector[String]] = None
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetLicense
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setlicense-schema
////////////////////////////////////////////////////////////////////////////////

case class SetLicense(
  shortName: String,
  name: String,
  spdxId: Option[String] = None,
  websiteUrl: URI
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetPollingSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setpollingsource-schema
////////////////////////////////////////////////////////////////////////////////

case class SetPollingSource(
  fetch: FetchStep,
  prepare: Option[Vector[PrepStep]] = None,
  read: ReadStep,
  preprocess: Option[Transform] = None,
  merge: MergeStrategy
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetTransform
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#settransform-schema
////////////////////////////////////////////////////////////////////////////////

case class SetTransform(
  inputs: Vector[TransformInput],
  transform: Transform
) extends MetadataEvent

////////////////////////////////////////////////////////////////////////////////
// SetVocab
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#setvocab-schema
////////////////////////////////////////////////////////////////////////////////

case class SetVocab(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None,
  offsetColumn: Option[String] = None
) extends MetadataEvent

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
// SourceState
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#sourcestate-schema
////////////////////////////////////////////////////////////////////////////////

case class SourceState(
  sourceName: String,
  kind: String,
  value: String
)

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
  name: String,
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
  datasetRef: DatasetRef,
  alias: Option[String] = None
)

////////////////////////////////////////////////////////////////////////////////
// TransformRequest
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transformrequest-schema
////////////////////////////////////////////////////////////////////////////////

case class TransformRequest(
  datasetId: DatasetId,
  datasetAlias: DatasetAlias,
  systemTime: Instant,
  vocab: DatasetVocabulary,
  transform: Transform,
  queryInputs: Vector[TransformRequestInput],
  nextOffset: Long,
  prevCheckpointPath: Option[Path] = None,
  newCheckpointPath: Path,
  newDataPath: Path
)

////////////////////////////////////////////////////////////////////////////////
// TransformRequestInput
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transformrequestinput-schema
////////////////////////////////////////////////////////////////////////////////

case class TransformRequestInput(
  datasetId: DatasetId,
  datasetAlias: DatasetAlias,
  queryAlias: String,
  vocab: DatasetVocabulary,
  offsetInterval: Option[OffsetInterval] = None,
  dataPaths: Vector[Path],
  schemaFile: Path,
  explicitWatermarks: Vector[Watermark]
)

////////////////////////////////////////////////////////////////////////////////
// TransformResponse
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transformresponse-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait TransformResponse

object TransformResponse {
  case class Progress(
    ) extends TransformResponse

  case class Success(
    newOffsetInterval: Option[OffsetInterval] = None,
    newWatermark: Option[Instant] = None
  ) extends TransformResponse

  case class InvalidQuery(
    message: String
  ) extends TransformResponse

  case class InternalError(
    message: String,
    backtrace: Option[String] = None
  ) extends TransformResponse
}

////////////////////////////////////////////////////////////////////////////////
// Watermark
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#watermark-schema
////////////////////////////////////////////////////////////////////////////////

case class Watermark(
  systemTime: Instant,
  eventTime: Instant
)
