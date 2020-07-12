/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core

import java.time.Instant

import spire.algebra.Order
import spire.math.{All, Empty, Interval}
import spire.math.interval.{Closed, Open, Unbound}

package object manifests {

  ////////////////////////////////////////////////////////////////////////////////
  // Extensions
  ////////////////////////////////////////////////////////////////////////////////

  implicit class DatasetSnapshotOps(s: DatasetSnapshot) {
    def kind: DatasetKind = {
      s.source match {
        case _: DatasetSource.Root       => DatasetKind.Root
        case _: DatasetSource.Derivative => DatasetKind.Derivative
      }
    }

    def dependsOn: Seq[DatasetID] = {
      s.source match {
        case d: DatasetSource.Derivative => d.inputs
        case _                           => Seq.empty
      }
    }
  }

  implicit class DatasetVocabularyOps(v: DatasetVocabulary) {
    def withDefaults(): DatasetVocabulary = {
      v.copy(
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
        case (k, Some(s: String))  => k -> s
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
        case (k, Some(s: String))  => k -> s
        case (k, Some(b: Boolean)) => k -> (if (b) "true" else "false")
      })
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Instant
  ////////////////////////////////////////////////////////////////////////////////

  implicit object InstantOrder extends Order[Instant] {
    override def compare(x: Instant, y: Instant): Int = x.compareTo(y)
  }

  implicit class IntervalOps[T](i: Interval[T]) {
    def format(): String = i match {
      case All() =>
        "(-inf, inf)"
      case Empty() =>
        "()"
      case i =>
        val lower = i.lowerBound match {
          case Unbound() => "(-inf"
          case Open(x)   => s"($x"
          case Closed(x) => s"[$x"
          case _ =>
            throw new RuntimeException(s"Unexpected: $i")
        }
        val upper = i.upperBound match {
          case Unbound() => "inf)"
          case Open(x)   => s"$x)"
          case Closed(x) => s"$x]"
          case _ =>
            throw new RuntimeException(s"Unexpected: $i")
        }
        s"$lower, $upper"
    }
  }

  object IntervalOps {
    def parse[T](s: String, parseFun: String => T)(
      implicit order: Order[T]
    ): Interval[T] = {
      val ss = s.replaceAll("\\s", "")
      if (ss == "()") {
        Interval.empty[T]
      } else {
        val split = ss.split(',')
        if (split.length != 2)
          throw new NumberFormatException("Cannot parse: " + s)

        val lower = split(0)
        val upper = split(1)
        val lowerVal = lower.substring(1)
        val upperVal = upper.substring(0, upper.length - 1)

        (lower.head, lowerVal, upperVal, upper.last) match {
          case ('(', "-inf", "inf", ')') =>
            Interval.all[T]
          case ('(', "-inf", y, ')') =>
            Interval.below(parseFun(y))
          case ('(', "-inf", y, ']') =>
            Interval.atOrBelow(parseFun(y))
          case ('(', x, "inf", ')') =>
            Interval.above(parseFun(x))
          case ('[', x, "inf", ')') =>
            Interval.atOrAbove(parseFun(x))
          case ('[', x, y, ']') =>
            Interval.closed(parseFun(x), parseFun(y))
          case ('(', x, y, ')') =>
            Interval.open(parseFun(x), parseFun(y))
          case ('[', x, y, ')') =>
            Interval.openUpper(parseFun(x), parseFun(y))
          case ('(', x, y, ']') =>
            Interval.openLower(parseFun(x), parseFun(y))
          case _ =>
            throw new NumberFormatException("Cannot parse: " + s)
        }
      }
    }
  }
}
