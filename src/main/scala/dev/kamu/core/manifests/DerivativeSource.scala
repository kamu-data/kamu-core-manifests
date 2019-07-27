package dev.kamu.core.manifests

case class DerivativeSource(
  /** Datasets that will be used as sources for this derivative */
  inputs: Vector[DerivativeInput],
  /** Processing steps that shape the data */
  steps: Vector[ProcessingStepSQL] = Vector.empty,
  /** Spark partitioning scheme */
  partitionBy: Vector[String] = Vector.empty
) extends Resource[DerivativeSource]

case class DerivativeInput(
  /** ID of the input dataset */
  id: DatasetID,
  /** Defines the mode in which this input should be open (see [[DerivativeInput.Mode]]) */
  mode: DerivativeInput.Mode = DerivativeInput.Mode.Stream
)

object DerivativeInput {
  sealed trait Mode
  object Mode {
    case object Stream extends Mode
    case object Batch extends Mode
  }
}
