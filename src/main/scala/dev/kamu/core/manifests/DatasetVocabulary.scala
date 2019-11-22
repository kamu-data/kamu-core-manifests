/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class DatasetVocabulary(
  systemTimeColumn: String = "systemTime",
  lastUpdatedTimeSystemColumn: String = "lastUpdatedSys",
  observationColumn: String = "observed",
  obsvAdded: String = "I",
  obsvChanged: String = "U",
  obsvRemoved: String = "D"
) extends Resource[DatasetVocabulary]
