/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.parsing.pureconfig.yaml

import java.beans.Introspector
import java.net.URI
import java.time.Instant

import org.apache.hadoop.fs.Path
import pureconfig._
import pureconfig.generic._
import spire.math.Interval

package object defaults {
  import dev.kamu.core.manifests._

  /** Camel case naming scheme */
  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](
      ConfigFieldMapping(CamelCase, CamelCase),
      useDefaultArgs = true,
      allowUnknownKeys = false
    )

  implicit val uriReader: ConfigReader[URI] = ConfigReader[String]
    .map(URI.create)

  implicit val uriWriter: ConfigWriter[URI] = ConfigWriter[String]
    .contramap((uri: URI) => uri.toString)

  implicit val pathReader: ConfigReader[Path] = ConfigReader[String]
    .map(s => new Path(URI.create(s)))

  implicit val pathWriter: ConfigWriter[Path] = ConfigWriter[String]
    .contramap((p: Path) => p.toString)

  implicit val instantReader: ConfigReader[Instant] = ConfigReader[String]
    .map(Instant.parse)

  implicit val instantWriter: ConfigWriter[Instant] = ConfigWriter[String]
    .contramap((i: Instant) => i.toString)

  implicit val intervalReader: ConfigReader[Interval[Instant]] =
    ConfigReader[String]
      .map(s => IntervalOps.parse(s, Instant.parse))

  implicit val intervalWriter: ConfigWriter[Interval[Instant]] =
    ConfigWriter[String].contramap(_.format())

  implicit val externalSourceKindHint: FieldCoproductHint[ExternalSourceKind] =
    new FieldCoproductHint[ExternalSourceKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val eventTimeKindHint: FieldCoproductHint[EventTimeKind] =
    new FieldCoproductHint[EventTimeKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val cachingKindHint: FieldCoproductHint[CachingKind] =
    new FieldCoproductHint[CachingKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val prepStepKindHint: FieldCoproductHint[PrepStepKind] =
    new FieldCoproductHint[PrepStepKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val readerKindHint: FieldCoproductHint[ReaderKind] =
    new FieldCoproductHint[ReaderKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val mergeStrategyHint: FieldCoproductHint[MergeStrategyKind] =
    new FieldCoproductHint[MergeStrategyKind]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

}
