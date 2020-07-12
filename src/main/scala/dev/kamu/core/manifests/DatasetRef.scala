/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

/** References a dataset stored remotely */
case class DatasetRef(
  /** ID of the volume that contains the dataset */
  remoteID: RemoteID,
  /** Username that identifies a particular tenant withing a multi-tenant remote */
  username: Option[Username] = None,
  /** ID of the dataset on the remote side */
  datasetID: DatasetID,
  /** Local alias of the dataset within a workspace */
  alias: Option[DatasetID] = None
)

object DatasetRef {
  private val remoteFormat = """([a-zA-Z0-9.]+)/([a-zA-Z0-9\.]+)""".r
  private val remoteMultiTenantFormat =
    """([a-zA-Z0-9.]+)/([a-zA-Z0-9.]+)/([a-zA-Z0-9\.]+)""".r

  def fromString(s: String): Option[DatasetRef] = {
    s match {
      case DatasetRef.remoteFormat(remoteID, datasetID) =>
        Some(
          DatasetRef(
            remoteID = RemoteID(remoteID),
            datasetID = DatasetID(datasetID)
          )
        )
      case DatasetRef.remoteMultiTenantFormat(remoteID, username, datasetID) =>
        Some(
          DatasetRef(
            remoteID = RemoteID(remoteID),
            username = Some(Username(username)),
            datasetID = DatasetID(datasetID)
          )
        )
      case _ => None
    }
  }
}
