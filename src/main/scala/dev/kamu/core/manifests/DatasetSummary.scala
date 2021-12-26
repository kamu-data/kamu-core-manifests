/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.time.Instant

/** Represents a snapshot of the dataset definition in a single point in time */
case class DatasetSummary(
  /** Unique identifier of the dataset */
  id: DatasetID,
  /** Alias of the dataset used in queries */
  name: DatasetName,
  /** Kind of a dataset (the kind cannot change throughout dataset's lifetime) */
  kind: DatasetKind,
  /** Set of immediate dependencies of this dataset */
  dependencies: Set[DatasetID] = Set.empty,
  /** Dataset vocabulary */
  vocab: Option[DatasetVocabulary] = None,
  /** The last time when this dataset had new data */
  lastPulled: Option[Instant],
  /** Total number of records in the dataset */
  numRecords: Long,
  /** Total size of data on disk */
  dataSize: Long
)
