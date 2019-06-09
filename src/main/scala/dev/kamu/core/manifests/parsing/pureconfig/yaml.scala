package dev.kamu.core.manifests.parsing.pureconfig

import java.io.{InputStream, OutputStream, PrintWriter}

import org.yaml.snakeyaml.{DumperOptions, Yaml}

import scala.reflect.ClassTag

import dev.kamu.core.manifests.ResourceBase

package object yaml {
  import pureconfig._
  import pureconfig.generic._
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
}
