/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.infra

import java.io.{InputStream, OutputStream}

import dev.kamu.core.manifests._
import org.apache.hadoop.fs.Path

case class TransformTaskConfig(
  datasetID: DatasetID,
  source: SourceKind.Derivative,
  inputSlices: Map[String, DataSlice],
  datasetVocabs: Map[String, DatasetVocabulary],
  datasetLayouts: Map[String, DatasetLayout],
  resultDir: Path
) extends Resource

case class TransformConfig(
  tasks: Vector[TransformTaskConfig]
) extends Resource

case class TransformResult(
  block: MetadataBlock,
  dataFileName: Option[String]
) extends Resource

@deprecated
object TransformConfig {
  import dev.kamu.core.manifests.parsing.pureconfig.yaml
  import yaml.defaults._
  import pureconfig.generic.auto._

  val configFileName = "transformConfig.yaml"

  def load(): TransformConfig = {
    yaml
      .load[Manifest[TransformConfig]](getConfigFromResources(configFileName))
      .content
  }

  def load(inputStream: InputStream): TransformConfig = {
    yaml.load[Manifest[TransformConfig]](inputStream).content
  }

  def load(config: String): TransformConfig = {
    yaml
      .load[Manifest[TransformConfig]](config)
      .content
  }

  def save(config: TransformConfig, outputStream: OutputStream): Unit = {
    yaml.save(Manifest(config), outputStream)
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
