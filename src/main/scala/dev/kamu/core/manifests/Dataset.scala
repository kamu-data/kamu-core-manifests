/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}

case class Dataset(
  /** Unique identifier of the dataset */
  id: DatasetID,
  /** If defined contains information about the root data source */
  rootPollingSource: Option[RootPollingSource] = None,
  /** If defined contains information about the derivative data source */
  derivativeSource: Option[DerivativeSource] = None,
  /** If defined contains a reference to an existing dataset stored remotely */
  remoteSource: Option[RemoteSource] = None,
  /** Dataset vocabulary */
  vocabulary: Option[DatasetVocabularyOverrides] = None
) extends Resource[Dataset] {

  Seq(rootPollingSource, derivativeSource, remoteSource).count(_.isDefined) match {
    case 1 =>
    case _ =>
      throw new ValidationException("Dataset must define exactly one source")
  }

  def kind: Dataset.Kind = {
    if (rootPollingSource.isDefined)
      Dataset.Kind.Root
    else if (derivativeSource.isDefined)
      Dataset.Kind.Derivative
    else
      Dataset.Kind.Remote
  }

  def dependsOn: Seq[DatasetID] = {
    if (derivativeSource.isDefined)
      derivativeSource.get.inputs.map(_.id)
    else
      Seq.empty
  }

  override def postLoad(): Dataset = {
    copy(
      rootPollingSource = rootPollingSource.map(_.postLoad()),
      derivativeSource = derivativeSource.map(_.postLoad()),
      remoteSource = remoteSource.map(_.postLoad())
    )
  }
}

object Dataset {
  sealed trait Kind
  object Kind {
    case object Root extends Kind
    case object Derivative extends Kind
    case object Remote extends Kind
  }
}
