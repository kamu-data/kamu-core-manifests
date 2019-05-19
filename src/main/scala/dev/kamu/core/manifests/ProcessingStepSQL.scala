package dev.kamu.core.manifests

case class ProcessingStepSQL(
  /* Name of the view that will contain the query result */
  view: String,
  /* A single SQL statement */
  query: String
)
