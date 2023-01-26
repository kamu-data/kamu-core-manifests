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
        inputs = v.inputs.map(_.withVocabDefaults())
      )
    }
  }

  implicit class ExecuteQueryInputOps(v: ExecuteQueryInput) {
    def withVocabDefaults(): ExecuteQueryInput = {
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
        "comment" -> r.comment,
        "header" -> r.header,
        "enforceSchema" -> r.enforceSchema.orElse(Some(false)),
        "inferSchema" -> r.inferSchema,
        "ignoreLeadingWhiteSpace" -> r.ignoreLeadingWhiteSpace,
        "ignoreTrailingWhiteSpace" -> r.ignoreTrailingWhiteSpace,
        "nullValue" -> r.nullValue,
        "emptyValue" -> r.emptyValue,
        "nanValue" -> r.nanValue,
        "positiveInf" -> r.positiveInf,
        "negativeInf" -> r.negativeInf,
        "dateFormat" -> r.dateFormat,
        "timestampFormat" -> r.timestampFormat,
        "multiLine" -> r.multiLine
      ).collect({
        case (k, Some(s: String)) => k -> s
        case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
      })
    }
  }

  implicit class JsonLinesOps(r: ReadStep.JsonLines) {
    def toSparkReaderOptions: Map[String, String] = {
      Map(
        "dateFormat" -> r.dateFormat,
        "encoding" -> r.encoding,
        "multiLine" -> r.multiLine,
        "primitivesAsString" -> r.primitivesAsString,
        "timestampFormat" -> r.timestampFormat
      ).collect({
        case (k, Some(s: String)) => k -> s
        case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
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
