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
import java.nio.file.Path

import better.files.File
import com.typesafe.config.{Config, ConfigObject}
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import pureconfig.error.ConfigReaderException
import pureconfig.module.yaml.YamlConfigSource

import scala.reflect.ClassTag

package object yaml {
  import pureconfig._

  def load[T: ClassTag](inputStream: InputStream)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val str = scala.io.Source.fromInputStream(inputStream).mkString
    load[T](str)
  }

  def load[T: ClassTag](str: String)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    YamlConfigSource.string(str).loadOrThrow[T]
  }

  def load[T: ClassTag](conf: Config)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    ConfigSource.fromConfig(conf).load[T] match {
      case Right(config)  => config
      case Left(failures) => throw new ConfigReaderException[Config](failures)
    }
  }

  def load[T: ClassTag](path: Path)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val inputStream = File(path).newInputStream
    val res = load[T](inputStream)
    inputStream.close()
    res
  }

  def loadFromResources[T: ClassTag](resourceName: String)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val stream = getClass.getClassLoader.getResourceAsStream(resourceName)

    if (stream == null)
      throw new ClassNotFoundException(
        s"Unable to locate $resourceName on classpath"
      )

    val res = load[T](stream)
    stream.close()
    res
  }

  def save[T: ClassTag](obj: T, outputStream: OutputStream)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): Unit = {
    val configValue = ConfigWriter[T].to(obj)
    val writer = new PrintWriter(outputStream)

    val options = new DumperOptions()
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    val yaml = new Yaml(options)
    yaml.dump(configValue.unwrapped(), writer)

    writer.flush()
  }

  def save[T: ClassTag](obj: T, path: Path)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): Unit = {
    val outputStream = File(path).newOutputStream()
    try {
      save(obj, outputStream)
    } catch {
      case e: Exception =>
        File(path).delete(swallowIOExceptions = true)
        throw e
    } finally {
      outputStream.close()
    }
  }

  def saveStr[T: ClassTag](obj: T)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): String = {
    val stream = new ByteArrayOutputStream()
    save(obj, stream)
    new String(stream.toByteArray, StandardCharsets.UTF_8)
  }

  def saveObj[T: ClassTag](obj: T)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): ConfigObject = {
    val configValue = ConfigWriter[T].to(obj)
    configValue.asInstanceOf[ConfigObject]
  }
}
