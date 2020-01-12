/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.parsing.pureconfig.yaml

import pureconfig._
import pureconfig.generic._
import pureconfig.generic.semiauto
import java.net.URI
import java.beans.Introspector

import dev.kamu.core.manifests.{
  CachingKind,
  DerivativeInput,
  EventTimeKind,
  ExternalSourceKind,
  MergeStrategyKind,
  PrepStepKind,
  ReaderKind
}
import org.apache.hadoop.fs.Path

package object defaults {

  /** Camel case naming scheme */
  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](
      ConfigFieldMapping(CamelCase, CamelCase),
      useDefaultArgs = true,
      allowUnknownKeys = false
    )

  implicit val uriReader = ConfigReader[String]
    .map(URI.create)

  implicit val uriWriter = ConfigWriter[String]
    .contramap((uri: URI) => uri.toString)

  implicit val pathReader = ConfigReader[String]
    .map(s => new Path(URI.create(s)))

  implicit val pathWriter = ConfigWriter[String]
    .contramap((p: Path) => p.toString)

  implicit val modeReader: ConfigReader[DerivativeInput.Mode] =
    semiauto.deriveEnumerationReader[DerivativeInput.Mode]

  implicit val modeWriter: ConfigWriter[DerivativeInput.Mode] =
    semiauto.deriveEnumerationWriter[DerivativeInput.Mode]

  implicit val externalSourceKindHint =
    new FieldCoproductHint[ExternalSourceKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }

  implicit val eventTimeKindHint =
    new FieldCoproductHint[EventTimeKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }

  implicit val cachingKindHint =
    new FieldCoproductHint[CachingKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }

  implicit val prepStepKindHint =
    new FieldCoproductHint[PrepStepKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }

  implicit val readerKindHint =
    new FieldCoproductHint[ReaderKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }

  implicit val mergeStrategyHint =
    new FieldCoproductHint[MergeStrategyKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name)
    }
}
