/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import org.scalatest._

class DatasetRefSpec extends FlatSpec with Matchers {

  "fromString" should "work" in {
    DatasetRef.fromString("dataset") should equal(None)

    DatasetRef.fromString("remote/dataset") should equal(
      Some(
        DatasetRef(
          remoteID = RemoteID("remote"),
          datasetID = DatasetID("dataset")
        )
      )
    )

    DatasetRef.fromString("remote/user/dataset") should equal(
      Some(
        DatasetRef(
          remoteID = RemoteID("remote"),
          username = Some(Username("user")),
          datasetID = DatasetID("dataset")
        )
      )
    )
  }

}
