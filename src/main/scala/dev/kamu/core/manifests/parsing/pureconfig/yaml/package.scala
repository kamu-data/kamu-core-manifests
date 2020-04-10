/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.parsing.pureconfig

import java.io.{ByteArrayOutputStream, InputStream, OutputStream, PrintWriter}
import java.nio.charset.StandardCharsets

import com.typesafe.config.{Config, ConfigObject}
import dev.kamu.core.manifests.Resource
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import pureconfig.error.ConfigReaderException

import scala.reflect.ClassTag

package object yaml {
  import pureconfig._
  import pureconfig.module.yaml.loadYamlOrThrow

  def load[T <: Resource: ClassTag](inputStream: InputStream)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val str = scala.io.Source.fromInputStream(inputStream).mkString
    load[T](str)
  }

  def load[T <: Resource: ClassTag](str: String)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val raw = loadYamlOrThrow[T](str)
    raw.postLoad().asInstanceOf[T]
  }

  def load[T <: Resource: ClassTag](conf: Config)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val raw = pureconfig.loadConfig[T](conf) match {
      case Right(config)  => config
      case Left(failures) => throw new ConfigReaderException[Config](failures)
    }
    raw.postLoad().asInstanceOf[T]
  }

  def save[T <: Resource: ClassTag](obj: T, outputStream: OutputStream)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): Unit = {
    val raw = obj.preSave().asInstanceOf[T]

    val configValue = ConfigWriter[T].to(raw)
    val writer = new PrintWriter(outputStream)

    val options = new DumperOptions()
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    val yaml = new Yaml(options)
    yaml.dump(configValue.unwrapped(), writer)

    writer.flush()
  }

  def saveStr[T <: Resource: ClassTag](obj: T)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): String = {
    val stream = new ByteArrayOutputStream()
    save(obj, stream)
    new String(stream.toByteArray, StandardCharsets.UTF_8)
  }

  def saveObj[T <: Resource: ClassTag](obj: T)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): ConfigObject = {
    val raw = obj.preSave().asInstanceOf[T]
    val configValue = ConfigWriter[T].to(raw)
    configValue.asInstanceOf[ConfigObject]
  }
}
