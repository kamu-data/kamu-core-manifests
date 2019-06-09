package dev.kamu.core.manifests

case class DatasetVocabulary(
  systemTimeColumn: String = "systemTime",
  lastUpdatedTimeSystemColumn: String = "lastUpdatedSys",
  observationColumn: String = "observed",
  obsvAdded: String = "added",
  obsvChanged: String = "changed",
  obsvRemoved: String = "removed"
) extends Resource[DatasetVocabulary]
