/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.infra

import java.io.InputStream
import java.time.Instant

import dev.kamu.core.manifests._
import org.apache.hadoop.fs.Path

case class IngestTask(
  datasetID: DatasetID,
  source: SourceKind.Root,
  datasetLayout: DatasetLayout,
  datasetVocab: DatasetVocabulary,
  dataToIngest: Path,
  eventTime: Option[Instant],
  metadataOutputDir: Path
) extends Resource

case class IngestConfig(
  tasks: Vector[IngestTask]
) extends Resource

object IngestConfig {
  import dev.kamu.core.manifests.parsing.pureconfig.yaml
  import dev.kamu.core.manifests.parsing.pureconfig.yaml.defaults._
  import pureconfig.generic.auto._

  val configFileName = "ingestConfig.yaml"

  def load(): IngestConfig = {
    yaml
      .load[Manifest[IngestConfig]](getConfigFromResources(configFileName))
      .content
  }

  private def getConfigFromResources(configFileName: String): InputStream = {

    val configStream =
      getClass.getClassLoader.getResourceAsStream(configFileName)

    if (configStream == null)
      throw new RuntimeException(
        s"Unable to locate $configFileName on classpath"
      )

    configStream
  }
}
