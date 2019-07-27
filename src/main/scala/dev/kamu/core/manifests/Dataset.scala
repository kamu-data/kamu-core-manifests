package dev.kamu.core.manifests

case class DatasetID(s: String) extends AnyVal {
  override def toString: String = s
}

case class Dataset(
  /** Unique identifier of the dataset */
  id: DatasetID,
  /** If defined contains information about the root data source */
  rootPollingSource: Option[RootPollingSource] = None,
  /** If defined contains information about the derivative data source */
  derivativeSource: Option[DerivativeSource] = None
) extends Resource[Dataset] {

  if (rootPollingSource.isDefined && derivativeSource.isDefined)
    throw new ValidationException("Dataset must have only one source")
  if (rootPollingSource.isEmpty && derivativeSource.isEmpty)
    throw new ValidationException("Dataset must define a source")

  def kind: Dataset.Kind = {
    if (rootPollingSource.isDefined)
      Dataset.Kind.Root
    else
      Dataset.Kind.Derivative
  }

  def dependsOn: Seq[DatasetID] = {
    if (derivativeSource.isDefined)
      derivativeSource.get.inputs.map(_.id)
    else
      Seq.empty
  }

  override def postLoad(): Dataset = {
    copy(
      rootPollingSource = rootPollingSource.map(_.postLoad()),
      derivativeSource = derivativeSource.map(_.postLoad())
    )
  }
}

object Dataset {
  sealed trait Kind
  object Kind {
    case object Root extends Kind
    case object Derivative extends Kind
  }
}
