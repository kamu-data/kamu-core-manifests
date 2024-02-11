/*
 * Copyright 2018 kamu.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
