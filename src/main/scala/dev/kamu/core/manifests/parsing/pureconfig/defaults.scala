package dev.kamu.core.manifests.parsing.pureconfig.yaml

import pureconfig._
import pureconfig.generic._
import pureconfig.generic.semiauto
import java.net.URI
import java.beans.Introspector

import dev.kamu.core.manifests.{
  DerivativeInput,
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
        Introspector.decapitalize(name.stripPrefix("ExternalSource"))
    }

  implicit val prepStepKindHint =
    new FieldCoproductHint[PrepStepKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name.stripPrefix("PrepStep"))
    }

  implicit val readerKindHint =
    new FieldCoproductHint[ReaderKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name.stripPrefix("Reader"))
    }

  implicit val mergeStrategyHint =
    new FieldCoproductHint[MergeStrategyKind]("kind") {
      override protected def fieldValue(name: String) =
        Introspector.decapitalize(name.stripPrefix("MergeStrategy"))
    }
}
