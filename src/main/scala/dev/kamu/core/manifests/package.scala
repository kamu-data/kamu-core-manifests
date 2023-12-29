/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core

package object manifests {

  ////////////////////////////////////////////////////////////////////////////////
  // Extensions
  ////////////////////////////////////////////////////////////////////////////////

  implicit class ExecuteQueryRequestOps(v: ExecuteQueryRequest) {
    def withVocabDefaults(): ExecuteQueryRequest = {
      v.copy(
        vocab = v.vocab.withDefaults(),
        queryInputs = v.queryInputs.map(_.withVocabDefaults())
      )
    }
  }

  implicit class ExecuteQueryRequestInputOps(v: ExecuteQueryRequestInput) {
    def withVocabDefaults(): ExecuteQueryRequestInput = {
      v.copy(
        vocab = v.vocab.withDefaults()
      )
    }
  }

  implicit class DatasetVocabularyOps(v: DatasetVocabulary) {
    def withDefaults(): DatasetVocabulary = {
      v.copy(
        offsetColumn = v.offsetColumn.orElse(Some("offset")),
        systemTimeColumn = v.systemTimeColumn.orElse(Some("system_time")),
        eventTimeColumn = v.eventTimeColumn.orElse(Some("event_time"))
      )
    }
  }

  implicit class CsvOps(r: ReadStep.Csv) {
    def toSparkReaderOptions: Map[String, String] = {
      Map(
        "sep" -> r.separator,
        "encoding" -> r.encoding,
        "quote" -> r.quote,
        "escape" -> r.escape,
        "header" -> r.header,
        "inferSchema" -> r.inferSchema,
        "nullValue" -> r.nullValue,
        "dateFormat" -> r.dateFormat,
        "timestampFormat" -> r.timestampFormat
      ).collect({
        case (k, Some(s: String))  => k -> s
        case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
      })
    }
  }

  implicit class NdJsonOps(r: ReadStep.NdJson) {
    def toSparkReaderOptions: Map[String, String] = {
      Map(
        "dateFormat" -> r.dateFormat,
        "encoding" -> r.encoding,
        "timestampFormat" -> r.timestampFormat
      ).collect({
        case (k, Some(s: String)) => k -> s
      })
    }
  }

  implicit class SnapshotOps(s: MergeStrategy.Snapshot) {
    def withDefaults(): MergeStrategy.Snapshot = {
      s.copy(
        observationColumn = Some(s.observationColumn.getOrElse("observed")),
        obsvAdded = Some(s.obsvAdded.getOrElse("I")),
        obsvChanged = Some(s.obsvChanged.getOrElse("U")),
        obsvRemoved = Some(s.obsvRemoved.getOrElse("D"))
      )
    }
  }
}
