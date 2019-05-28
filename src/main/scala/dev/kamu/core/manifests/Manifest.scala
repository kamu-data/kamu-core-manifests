package dev.kamu.core.manifests

case class Manifest[T](
  /** Version of the application this manifest was written for.
    *
    * Tracking this version will allow us to attempt an upgrade of
    * older manifests istead of matching them to latest format and
    * erroring out.
    */
  apiVersion: String,
  /** Name of the manifest.
    *
    * This prevents accidentally taking one kind of a manifest
    * for another in cases when their schemas are similar.
    */
  kind: String,
  /** Here goes the actual manifest data */
  content: T
)
