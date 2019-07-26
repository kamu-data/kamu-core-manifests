package dev.kamu.core.manifests

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}
