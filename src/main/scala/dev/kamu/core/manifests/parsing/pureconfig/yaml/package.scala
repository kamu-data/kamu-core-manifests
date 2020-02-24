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

import dev.kamu.core.manifests.ResourceBase
import org.yaml.snakeyaml.{DumperOptions, Yaml}

import scala.reflect.ClassTag

package object yaml {
  import pureconfig._
  import pureconfig.module.yaml.loadYamlOrThrow

  def load[T <: ResourceBase[T]: ClassTag](inputStream: InputStream)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val str = scala.io.Source.fromInputStream(inputStream).mkString
    load[T](str)
  }

  def load[T <: ResourceBase[T]: ClassTag](str: String)(
    implicit reader: Derivation[ConfigReader[T]]
  ): T = {
    val raw = loadYamlOrThrow[T](str)
    raw.postLoad()
  }

  def save[T <: ResourceBase[T]: ClassTag](obj: T, outputStream: OutputStream)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): Unit = {
    val raw = obj.preSave()

    val configValue = ConfigWriter[T].to(raw)
    val writer = new PrintWriter(outputStream)

    val options = new DumperOptions()
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    val yaml = new Yaml(options)
    yaml.dump(configValue.unwrapped(), writer)

    writer.flush()
  }

  def saveStr[T <: ResourceBase[T]: ClassTag](obj: T)(
    implicit derivation: Derivation[ConfigWriter[T]]
  ): String = {
    val stream = new ByteArrayOutputStream()
    save(obj, stream)
    new String(stream.toByteArray, StandardCharsets.UTF_8)
  }
}
