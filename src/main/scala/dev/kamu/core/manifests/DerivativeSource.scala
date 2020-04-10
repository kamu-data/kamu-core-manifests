/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import com.typesafe.config.ConfigObject
import pureconfig.error.{
  ConfigReaderException,
  ConfigReaderFailures,
  ConvertFailure,
  KeyNotFound
}

case class DerivativeSource(
  /** Datasets that will be used as sources for this derivative */
  inputs: Vector[DerivativeInput],
  /** Engine-specific processing queries that shape the resulting data (see [[TransformKind]]) */
  transform: ConfigObject
  /** TODO: Output mode (e,g, Spark's Append vs Update)?  */
) extends Resource {

  override def postLoad(): AnyRef = {
    if (!transform.containsKey("engine"))
      throw new ConfigReaderException[DerivativeSource](
        ConfigReaderFailures(ConvertFailure(KeyNotFound("engine"), None, ""))
      )
    super.postLoad()
  }

  def transformEngine: String = {
    transform.get("engine").unwrapped().asInstanceOf[String]
  }

}

case class DerivativeInput(
  /** ID of the input dataset */
  id: DatasetID
  /** TODO: Watermarking configuration? */
)
