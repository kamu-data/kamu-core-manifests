/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

trait ResourceBase[R <: ResourceBase[R]] {
  val resourceName = getClass.getSimpleName

  def postLoad(): R = { this.asInstanceOf[R] }

  def preSave(): R = { this.asInstanceOf[R] }
}

trait Resource[R <: Resource[R]] extends ResourceBase[R] {
  def asManifest: Manifest[R] = {
    Manifest[R](
      apiVersion = 1,
      kind = getClass.getSimpleName,
      content = this.asInstanceOf[R]
    )
  }
}
