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

package dev.kamu.core.manifests.parsing.pureconfig

import java.io.{
  ByteArrayOutputStream,
  FileInputStream,
  FileOutputStream,
  InputStream,
  OutputStream,
  PrintWriter
}
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import com.typesafe.config.{Config, ConfigObject}
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import pureconfig.error.ConfigReaderException
import pureconfig.module.yaml.YamlConfigSource

import scala.reflect.ClassTag

package object yaml {
  import pureconfig._

  def load[T: ClassTag](inputStream: InputStream)(
    implicit reader: ConfigReader[T]
  ): T = {
    val str = scala.io.Source.fromInputStream(inputStream).mkString
    load[T](str)
  }

  def load[T: ClassTag](str: String)(
    implicit reader: ConfigReader[T]
  ): T = {
    YamlConfigSource.string(str).loadOrThrow[T]
  }

  def load[T: ClassTag](conf: Config)(
    implicit reader: ConfigReader[T]
  ): T = {
    ConfigSource.fromConfig(conf).load[T] match {
      case Right(config)  => config
      case Left(failures) => throw new ConfigReaderException[Config](failures)
    }
  }

  def load[T: ClassTag](path: Path)(
    implicit reader: ConfigReader[T]
  ): T = {
    val inputStream = new FileInputStream(path.toFile)
    val res = load[T](inputStream)
    inputStream.close()
    res
  }

  def loadFromResources[T: ClassTag](resourceName: String)(
    implicit reader: ConfigReader[T]
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
    implicit derivation: ConfigWriter[T]
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
    implicit derivation: ConfigWriter[T]
  ): Unit = {
    val outputStream = new FileOutputStream(path.toFile)
    try {
      save(obj, outputStream)
    } catch {
      case e: Exception =>
        path.toFile.delete()
        throw e
    } finally {
      outputStream.close()
    }
  }

  def saveStr[T: ClassTag](obj: T)(
    implicit derivation: ConfigWriter[T]
  ): String = {
    val stream = new ByteArrayOutputStream()
    save(obj, stream)
    new String(stream.toByteArray, StandardCharsets.UTF_8)
  }

  def saveObj[T: ClassTag](obj: T)(
    implicit derivation: ConfigWriter[T]
  ): ConfigObject = {
    val configValue = ConfigWriter[T].to(obj)
    configValue.asInstanceOf[ConfigObject]
  }
}
