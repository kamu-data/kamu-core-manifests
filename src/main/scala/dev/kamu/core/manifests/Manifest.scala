/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class Manifest[T <: Resource[T]](
  /** Version of the application this manifest was written for.
    *
    * Tracking this version will allow us to attempt an upgrade of
    * older manifests istead of matching them to latest format and
    * erroring out.
    */
  apiVersion: Int,
  /** Name of the manifest.
    *
    * This prevents accidentally taking one kind of a manifest
    * for another in cases when their schemas are similar.
    */
  kind: String,
  /** Here goes the actual manifest data */
  content: T
) extends ResourceBase[Manifest[T]] {
  override def postLoad() = {
    if (kind != content.resourceName)
      throw new RuntimeException(
        s"Got manifest for kind $kind but expected ${content.resourceName}"
      )

    this.copy(content = content.postLoad())
  }

  override def preSave() = {
    this.copy(content = content.preSave())
  }
}
