package dev.kamu.core.manifests.infra

import dev.kamu.core.manifests._

case class ExecuteQueryRequest(
  datasetID: DatasetID,
  source: SourceKind.Derivative,
  inputSlices: Map[String, DataSlice],
  datasetVocabs: Map[String, DatasetVocabulary],
  datasetLayouts: Map[String, DatasetLayout]
) extends Resource

case class ExecuteQueryResult(
  block: MetadataBlock,
  dataFileName: Option[String]
) extends Resource
