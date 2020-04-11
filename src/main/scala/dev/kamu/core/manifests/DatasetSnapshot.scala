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
  /** Contains information about the source of data (see [[SourceKind]]) */
  source: SourceKind,
  /** Dataset vocabulary */
  vocabulary: Option[DatasetVocabularyOverrides] = None
) extends Resource {

  def kind: DatasetKind = {
    source match {
      case _: SourceKind.Root       => DatasetKind.Root
      case _: SourceKind.Derivative => DatasetKind.Derivative
    }
  }

  def dependsOn: Seq[DatasetID] = {
    source match {
      case d: SourceKind.Derivative => d.inputs.map(_.id)
      case _                        => Seq.empty
    }
  }

  override def postLoad(): DatasetSnapshot = {
    copy(source = source.postLoad().asInstanceOf[SourceKind])
  }
}
