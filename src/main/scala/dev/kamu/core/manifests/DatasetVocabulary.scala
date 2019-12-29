/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class DatasetVocabulary(
  systemTimeColumn: String = "system_time",
  eventTimeColumn: String = "event_time",
  corruptRecordColumn: String = "__corrupt_record__",
  observationColumn: String = "observed",
  obsvAdded: String = "I",
  obsvChanged: String = "U",
  obsvRemoved: String = "D"
) extends Resource[DatasetVocabulary] {}

case class DatasetVocabularyOverrides(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None,
  lastUpdatedTimeSystemColumn: Option[String] = None,
  corruptRecordColumn: Option[String] = None,
  observationColumn: Option[String] = None,
  obsvAdded: Option[String] = None,
  obsvChanged: Option[String] = None,
  obsvRemoved: Option[String] = None
) {
  def asDatasetVocabulary(): DatasetVocabulary = {
    val vocab = DatasetVocabulary()
    vocab.copy(
      systemTimeColumn = systemTimeColumn.getOrElse(vocab.systemTimeColumn),
      eventTimeColumn = eventTimeColumn.getOrElse(vocab.eventTimeColumn),
      corruptRecordColumn =
        corruptRecordColumn.getOrElse(vocab.corruptRecordColumn),
      observationColumn = observationColumn.getOrElse(vocab.observationColumn),
      obsvAdded = obsvAdded.getOrElse(vocab.obsvAdded),
      obsvChanged = obsvChanged.getOrElse(vocab.obsvChanged),
      obsvRemoved = obsvRemoved.getOrElse(vocab.obsvRemoved)
    )
  }
}
