package dev.kamu.core.manifests

case class DatasetVocabulary(
  systemTimeColumn: String = "systemTime",
  lastUpdatedTimeSystemColumn: String = "lastUpdatedSys",
  observationColumn: String = "observed",
  obsvAdded: String = "I",
  obsvChanged: String = "U",
  obsvRemoved: String = "D"
) extends Resource[DatasetVocabulary]
