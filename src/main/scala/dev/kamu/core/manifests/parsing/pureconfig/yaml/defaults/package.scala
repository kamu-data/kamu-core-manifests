/*
 * Copyright 2018 kamu.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.kamu.core.manifests.parsing.pureconfig.yaml

import java.beans.Introspector
import java.net.URI
import java.time.Instant

import pureconfig._
import pureconfig.generic._
import pureconfig.generic.semiauto

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

  implicit val datasetKindReader: ConfigReader[DatasetKind] =
    semiauto.deriveEnumerationReader[DatasetKind](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val datasetKindWriter: ConfigWriter[DatasetKind] =
    semiauto.deriveEnumerationWriter[DatasetKind](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val sourceOrderingReader: ConfigReader[SourceOrdering] =
    semiauto.deriveEnumerationReader[SourceOrdering](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val sourceOrderingWriter: ConfigWriter[SourceOrdering] =
    semiauto.deriveEnumerationWriter[SourceOrdering](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val compressionFormatReader: ConfigReader[CompressionFormat] =
    semiauto.deriveEnumerationReader[CompressionFormat](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val compressionFormatWriter: ConfigWriter[CompressionFormat] =
    semiauto.deriveEnumerationWriter[CompressionFormat](
      ConfigFieldMapping(PascalCase, PascalCase)
    )

  implicit val metadataEventHint: FieldCoproductHint[MetadataEvent] =
    new FieldCoproductHint[MetadataEvent]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val fetchStepHint: FieldCoproductHint[FetchStep] =
    new FieldCoproductHint[FetchStep]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val eventTimeSourceHint: FieldCoproductHint[EventTimeSource] =
    new FieldCoproductHint[EventTimeSource]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val sourceCachingHint: FieldCoproductHint[SourceCaching] =
    new FieldCoproductHint[SourceCaching]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val prepStepHint: FieldCoproductHint[PrepStep] =
    new FieldCoproductHint[PrepStep]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val readStepHint: FieldCoproductHint[ReadStep] =
    new FieldCoproductHint[ReadStep]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val mergeStrategyHint: FieldCoproductHint[MergeStrategy] =
    new FieldCoproductHint[MergeStrategy]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val transformHint: FieldCoproductHint[Transform] =
    new FieldCoproductHint[Transform]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val rawQueryResponseHint: FieldCoproductHint[RawQueryResponse] =
    new FieldCoproductHint[RawQueryResponse]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }

  implicit val transformResponseHint: FieldCoproductHint[TransformResponse] =
    new FieldCoproductHint[TransformResponse]("kind") {
      override protected def fieldValue(name: String): String =
        name
    }
}
