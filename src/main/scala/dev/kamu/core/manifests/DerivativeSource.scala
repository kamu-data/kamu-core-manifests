/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class DerivativeSource(
  /** Datasets that will be used as sources for this derivative */
  inputs: Vector[DerivativeInput],
  /** Processing steps that shape the data */
  steps: Vector[ProcessingStepSQL] = Vector.empty,
  /** Spark partitioning scheme */
  partitionBy: Vector[String] = Vector.empty
) extends Resource[DerivativeSource]

case class DerivativeInput(
  /** ID of the input dataset */
  id: DatasetID,
  /** Defines the mode in which this input should be open (see [[DerivativeInput.Mode]]) */
  mode: DerivativeInput.Mode = DerivativeInput.Mode.Stream
)

object DerivativeInput {
  sealed trait Mode
  object Mode {
    case object Stream extends Mode
    case object Batch extends Mode
  }
}
