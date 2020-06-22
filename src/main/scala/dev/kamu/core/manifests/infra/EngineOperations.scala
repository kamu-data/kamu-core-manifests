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
import org.apache.hadoop.fs.Path

///////////////////////////////////////////////////////////////////////////////
// Ingest
///////////////////////////////////////////////////////////////////////////////

case class IngestRequest(
  datasetID: DatasetID,
  source: SourceKind.Root,
  datasetLayout: DatasetLayout,
  datasetVocab: DatasetVocabulary,
  dataToIngest: Path,
  eventTime: Option[Instant]
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
  inputSlices: Map[String, DataSlice],
  inputProperties: Map[String, InputProperties],
  datasetVocabs: Map[String, DatasetVocabulary],
  datasetLayouts: Map[String, DatasetLayout]
) extends Resource

case class ExecuteQueryResult(
  block: MetadataBlock,
  dataFileName: Option[String]
) extends Resource

case class InputProperties(
  /** Advances the watermark to the specified value at the end of processing */
  explicitWatermark: Option[Instant]
)
