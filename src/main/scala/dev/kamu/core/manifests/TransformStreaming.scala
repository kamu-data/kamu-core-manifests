package dev.kamu.core.manifests

case class TransformStreaming(
  /** ID of the new derivative dataset */
  id: DatasetID,
  /** Datasets that will be used as sources for this derivative */
  inputs: Vector[TransformStreamingInput],
  /** Processing steps that shape the data */
  steps: Vector[ProcessingStepSQL] = Vector.empty,
  /** Spark partitioning scheme */
  partitionBy: Vector[String] = Vector.empty
) extends DataSource
    with Resource[TransformStreaming] {

  override def dependsOn: Seq[DatasetID] = {
    inputs.map(_.id)
  }

}

case class TransformStreamingInput(
  /** ID of the input dataset */
  id: DatasetID,
  /*** Defines the mode in which this input should be open
    *
    * Valid values are:
    *  - batch
    *  - stream
    */
  mode: String = "stream"
)
