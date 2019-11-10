package dev.kamu.core.manifests

case class RootPollingSource(
  /** Determines where data is sourced from (see [[ExternalSourceKind]]) */
  fetch: ExternalSourceKind,
  /** Defines how raw data is prepared before reading (see [[PrepStepKind]]) */
  prepare: Vector[PrepStepKind] = Vector.empty,
  /** Defines how data is read into structured format (see [[ReaderKind]]) */
  read: ReaderKind,
  /** Pre-processing steps to shape the data */
  preprocess: Vector[ProcessingStepSQL] = Vector.empty,
  /** Determines how newly-ingested data should be merged with existing history (see [[MergeStrategyKind]]) */
  merge: MergeStrategyKind,
  /** Collapse partitions of the result to specified number.
    *
    * If zero - the step will be skipped
    */
  coalesce: Int = 1
) extends Resource[RootPollingSource] {

  override def postLoad(): RootPollingSource = {
    copy(read = read.postLoad())
  }
}
