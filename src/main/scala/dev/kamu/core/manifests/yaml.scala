package dev.kamu.core.manifests

import java.io.{OutputStream, PrintWriter}

import pureconfig._
import org.yaml.snakeyaml.{DumperOptions, Yaml}

package object yaml {
  def saveYaml[Config](obj: Config, outputStream: OutputStream)(
    implicit derivation: Derivation[ConfigWriter[Config]]
  ): Unit = {
    val configValue = ConfigWriter[Config].to(obj)
    val writer = new PrintWriter(outputStream)

    val options = new DumperOptions()
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    val yaml = new Yaml(options)
    yaml.dump(configValue.unwrapped(), writer)

    writer.flush()
  }

}
