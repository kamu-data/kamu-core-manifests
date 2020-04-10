/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

import java.net.URI

case class VolumeID(s: String) extends AnyVal {
  override def toString: String = s
}

case class Volume(
  /** ID of the volume */
  id: VolumeID,
  /** Location of the volume */
  url: URI
) extends Resource
