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
  eventTimeColumn: Option[String] = None,
  corruptRecordColumn: String = "__corrupt_record__"
) extends Resource {}

case class DatasetVocabularyOverrides(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None,
  corruptRecordColumn: Option[String] = None
) {
  def asDatasetVocabulary(): DatasetVocabulary = {
    val vocab = DatasetVocabulary()
    vocab.copy(
      systemTimeColumn = systemTimeColumn.getOrElse(vocab.systemTimeColumn),
      eventTimeColumn = eventTimeColumn,
      corruptRecordColumn =
        corruptRecordColumn.getOrElse(vocab.corruptRecordColumn)
    )
  }
}
