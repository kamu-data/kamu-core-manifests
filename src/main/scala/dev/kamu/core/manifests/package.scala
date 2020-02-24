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
