/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class Manifest[T](
  /** Name of the manifest.
    *
    * This prevents accidentally taking one kind of a manifest
    * for another in cases when their schemas are similar.
    */
  kind: String,
  /** Version of the application this manifest was written for.
    *
    * Tracking this version will allow us to attempt an upgrade of
    * older manifests istead of matching them to latest format and
    * erroring out.
    */
  version: Int,
  /** Here goes the actual resource data */
  content: T
) {
  if (kind != content.getClass.getSimpleName)
    throw new RuntimeException(
      s"Expected manifest for kind ${content.getClass.getSimpleName} but got $kind"
    )
}

object Manifest {
  def apply[T](resource: T): Manifest[T] = {
    Manifest(
      kind = resource.getClass.getSimpleName,
      version = 1,
      content = resource
    )
  }
}
