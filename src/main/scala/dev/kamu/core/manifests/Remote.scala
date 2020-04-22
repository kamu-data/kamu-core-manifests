/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI

case class RemoteID(s: String) extends AnyVal {
  override def toString: String = s
}

case class Username(s: String) extends AnyVal {
  override def toString: String = s
}

case class Remote(
  /** ID of the remote */
  id: RemoteID,
  /** Location of the remote */
  url: URI
) extends Resource
