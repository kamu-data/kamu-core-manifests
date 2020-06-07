/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.infra

import dev.kamu.core.manifests._

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  source: SourceKind.Derivative,
  inputSlices: Map[String, DataSlice],
  datasetVocabs: Map[String, DatasetVocabulary],
  datasetLayouts: Map[String, DatasetLayout]
) extends Resource

case class ExecuteQueryResult(
  block: MetadataBlock,
  dataFileName: Option[String]
) extends Resource
