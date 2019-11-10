package dev.kamu.core.manifests

import java.net.URI

sealed trait ExternalSourceKind

case class ExternalSourceFetchUrl(
  /** Data source location */
  url: URI
) extends ExternalSourceKind
