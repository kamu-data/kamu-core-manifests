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
  source: DatasetSource.Root,
  datasetVocab: DatasetVocabulary,
  checkpointsDir: String,
  dataDir: String,
  outDataPath: String
)

case class IngestResult(
  block: MetadataBlock
)

///////////////////////////////////////////////////////////////////////////////
// Execute Query
///////////////////////////////////////////////////////////////////////////////

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  source: DatasetSource.Derivative,
  datasetVocabs: Map[String, DatasetVocabulary],
  inputSlices: Map[String, InputDataSlice],
  dataDirs: Map[String, String],
  checkpointsDir: String,
  outDataPath: String
)

case class ExecuteQueryResult(
  block: MetadataBlock,
  dataFileName: Option[String]
)

case class InputDataSlice(
  interval: Interval[Instant],
  explicitWatermarks: Vector[Watermark] = Vector.empty
)

case class Watermark(
  systemTime: Instant,
  eventTime: Instant
)
