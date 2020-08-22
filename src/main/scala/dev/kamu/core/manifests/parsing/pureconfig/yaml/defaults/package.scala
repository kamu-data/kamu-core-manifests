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

import pureconfig._
import pureconfig.generic._
import pureconfig.generic.semiauto
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

  implicit val instantReader: ConfigReader[Instant] = ConfigReader[String]
    .map(Instant.parse)

  implicit val instantWriter: ConfigWriter[Instant] = ConfigWriter[String]
    .contramap((i: Instant) => i.toString)

  implicit val intervalReader: ConfigReader[Interval[Instant]] =
    ConfigReader[String]
      .map(s => IntervalOps.parse(s, Instant.parse))

  implicit val intervalWriter: ConfigWriter[Interval[Instant]] =
    ConfigWriter[String].contramap(_.format())

  implicit val datasetKindReader: ConfigReader[DatasetKind] =
    semiauto.deriveEnumerationReader[DatasetKind]

  implicit val datasetKindWriter: ConfigWriter[DatasetKind] =
    semiauto.deriveEnumerationWriter[DatasetKind]

  implicit val sourceOrderingReader: ConfigReader[SourceOrdering] =
    semiauto.deriveEnumerationReader[SourceOrdering](
      ConfigFieldMapping(PascalCase, CamelCase)
    )

  implicit val sourceOrderingWriter: ConfigWriter[SourceOrdering] =
    semiauto.deriveEnumerationWriter[SourceOrdering](
      ConfigFieldMapping(PascalCase, CamelCase)
    )

  implicit val compressionFormatReader: ConfigReader[CompressionFormat] =
    semiauto.deriveEnumerationReader[CompressionFormat](
      ConfigFieldMapping(PascalCase, CamelCase)
    )

  implicit val compressionFormatWriter: ConfigWriter[CompressionFormat] =
    semiauto.deriveEnumerationWriter[CompressionFormat](
      ConfigFieldMapping(PascalCase, CamelCase)
    )

  implicit val datasetSourceHint: FieldCoproductHint[DatasetSource] =
    new FieldCoproductHint[DatasetSource]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val fetchStepHint: FieldCoproductHint[FetchStep] =
    new FieldCoproductHint[FetchStep]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val eventTimeSourceHint: FieldCoproductHint[EventTimeSource] =
    new FieldCoproductHint[EventTimeSource]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val sourceCachingHint: FieldCoproductHint[SourceCaching] =
    new FieldCoproductHint[SourceCaching]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val prepStepHint: FieldCoproductHint[PrepStep] =
    new FieldCoproductHint[PrepStep]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val readStepHint: FieldCoproductHint[ReadStep] =
    new FieldCoproductHint[ReadStep]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val mergeStrategyHint: FieldCoproductHint[MergeStrategy] =
    new FieldCoproductHint[MergeStrategy]("kind") {
      override protected def fieldValue(name: String): String =
        Introspector.decapitalize(name)
    }

  implicit val transformHint: ProductHint[Transform] =
    ProductHint[Transform](allowUnknownKeys = true)
}
