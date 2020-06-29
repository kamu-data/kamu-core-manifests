/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.infra

import java.time.Instant

import dev.kamu.core.manifests._
import spire.math.Interval

///////////////////////////////////////////////////////////////////////////////
// Ingest
///////////////////////////////////////////////////////////////////////////////

case class IngestRequest(
  datasetID: DatasetID,
  ingestPath: String,
  eventTime: Option[Instant],
  source: SourceKind.Root,
  datasetVocab: DatasetVocabulary,
  dataDir: String
) extends Resource

case class IngestResult(
  block: MetadataBlock
) extends Resource

///////////////////////////////////////////////////////////////////////////////
// Execute Query
///////////////////////////////////////////////////////////////////////////////

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  source: SourceKind.Derivative,
  datasetVocabs: Map[String, DatasetVocabulary],
  inputSlices: Map[String, InputDataSlice],
  dataDirs: Map[String, String],
  checkpointDir: String
) extends Resource

case class ExecuteQueryResult(
  block: MetadataBlock,
  dataFileName: Option[String]
) extends Resource

case class InputDataSlice(
  interval: Interval[Instant],
  explicitWatermarks: Vector[Watermark] = Vector.empty
)

case class Watermark(
  systemTime: Instant,
  eventTime: Instant
)
