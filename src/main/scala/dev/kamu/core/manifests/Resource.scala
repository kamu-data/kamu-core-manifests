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
