/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

/** Represents a snapshot of the dataset definition in a single point in time */
case class DatasetSnapshot(
  /** Unique identifier of the dataset */
  id: DatasetID,
  /** If defined contains information about the root data source */
  rootPollingSource: Option[RootPollingSource] = None,
  /** If defined contains information about the derivative data source */
  derivativeSource: Option[DerivativeSource] = None,
  /** Dataset vocabulary */
  vocabulary: Option[DatasetVocabularyOverrides] = None
) extends Resource[DatasetSnapshot] {

  Seq(rootPollingSource, derivativeSource).count(_.isDefined) match {
    case 1 =>
    case _ =>
      throw new ValidationException("Dataset must define exactly one source")
  }

  def kind: DatasetKind = {
    if (rootPollingSource.isDefined)
      DatasetKind.Root
    else if (derivativeSource.isDefined)
      DatasetKind.Derivative
    else
      DatasetKind.Remote
  }

  def dependsOn: Seq[DatasetID] = {
    if (derivativeSource.isDefined)
      derivativeSource.get.inputs.map(_.id)
    else
      Seq.empty
  }

  override def postLoad(): DatasetSnapshot = {
    copy(
      rootPollingSource = rootPollingSource.map(_.postLoad()),
      derivativeSource = derivativeSource.map(_.postLoad())
    )
  }
}
