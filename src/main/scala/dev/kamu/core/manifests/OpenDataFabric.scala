/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI
import java.time.Instant

import com.typesafe.config.ConfigObject
import spire.math.Interval
import pureconfig.generic.auto._
import dev.kamu.core.manifests.parsing.pureconfig.yaml.defaults._
import dev.kamu.core.manifests.parsing.pureconfig.yaml

////////////////////////////////////////////////////////////////////////////////
// WARNING: This file is auto-generated from Open Data Fabric Schemas
// See: http://opendatafabric.org/
////////////////////////////////////////////////////////////////////////////////

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}

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
    preprocess: Option[ConfigObject] = None,
    merge: MergeStrategy
  ) extends DatasetSource {
    val preprocessPartial: Option[Transform] =
      preprocess.map(c => yaml.load[Transform](c.toConfig))
  }

  case class Derivative(
    inputs: Vector[DatasetID],
    transform: ConfigObject
  ) extends DatasetSource {
    val transformPartial: Transform =
      yaml.load[Transform](transform.toConfig)
  }
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
// SourceCaching
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#sourcecaching-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait SourceCaching

object SourceCaching {
  case class Forever(
    ) extends SourceCaching
}

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
// DataSlice
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#dataslice-schema
////////////////////////////////////////////////////////////////////////////////

case class DataSlice(
  hash: String,
  interval: Interval[Instant],
  numRecords: Long
)

////////////////////////////////////////////////////////////////////////////////
// SourceOrdering
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#sourceordering-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait SourceOrdering

object SourceOrdering {
  case class ByEventTime(
    ) extends SourceOrdering

  case class ByName(
    ) extends SourceOrdering
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
  prevBlockHash: String,
  systemTime: Instant,
  outputSlice: Option[DataSlice] = None,
  outputWatermark: Option[Instant] = None,
  //outputSchema: Option[Schema] = None,
  inputSlices: Option[Vector[DataSlice]] = None,
  source: Option[DatasetSource] = None
)

////////////////////////////////////////////////////////////////////////////////
// ReadStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#readstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait ReadStep

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
// Transform
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#transform-schema
////////////////////////////////////////////////////////////////////////////////

case class Transform(
  engine: String
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
    cache: Option[SourceCaching] = None
  ) extends FetchStep

  case class FilesGlob(
    path: String,
    eventTime: Option[EventTimeSource] = None,
    cache: Option[SourceCaching] = None,
    order: Option[SourceOrdering] = None
  ) extends FetchStep
}

////////////////////////////////////////////////////////////////////////////////
// PrepStep
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#prepstep-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait PrepStep

object PrepStep {
  case class Decompress(
    format: String,
    subPath: Option[String] = None
  ) extends PrepStep

  case class Pipe(
    command: Vector[String]
  ) extends PrepStep
}

////////////////////////////////////////////////////////////////////////////////
// EventTimeSource
// https://github.com/kamu-data/open-data-fabric/blob/master/open-data-fabric.md#eventtimesource-schema
////////////////////////////////////////////////////////////////////////////////

sealed trait EventTimeSource

object EventTimeSource {
  case class FromPath(
    pattern: String,
    timestampFormat: Option[String] = None
  ) extends EventTimeSource
}
