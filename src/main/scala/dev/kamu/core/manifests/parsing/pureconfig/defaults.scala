package dev.kamu.core.manifests.parsing.pureconfig.yaml

import pureconfig._
import pureconfig.generic._
import java.net.URI
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
}
