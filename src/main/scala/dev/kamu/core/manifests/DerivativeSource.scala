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
  steps: Vector[ProcessingStepKind] = Vector.empty
) extends Resource[DerivativeSource]

case class DerivativeInput(
  /** ID of the input dataset */
  id: DatasetID
)
