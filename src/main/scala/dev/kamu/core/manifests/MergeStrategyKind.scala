/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

sealed trait MergeStrategyKind

/** Append merge strategy.
  *
  * Under this strategy polled data will be appended in its original form
  * to the already ingested data without modifications. Optionally can add
  * a system time column.
  */
case class MergeStrategyAppend(
  ) extends MergeStrategyKind

/** Ledger merge strategy.
  *
  * This strategy should be used for data dumps containing append-only event
  * streams. New data dumps can have new rows added, but once data already
  * made it into one dump it never changes or disappears.
  *
  * A system time column will be added to the data to indicate the time
  * when the record was observed first by the system.
  *
  * It relies on a user-specified primary key column to identify which records
  * were already seen and not duplicate them.
  *
  * It will always preserve all columns from existing and new snapshots, so
  * the set of columns can only grow.
  */
case class MergeStrategyLedger(
  /** Names of the columns that uniquely identify the record throughout its lifetime */
  primaryKey: Vector[String]
) extends MergeStrategyKind

/** Snapshot merge strategy.
  *
  * This strategy can be used for data dumps that are taken periodically
  * and contain only the latest state of the observed entity or system.
  * Over time such dumps can have new rows added, and old rows either removed
  * or modified.
  *
  * This strategy transforms snapshot data into an append-only event stream
  * where data already added is immutable. It does so by treating rows in
  * snapshots as "observation" events and adding an "observed" column
  * that will contain:
  *   - "I" - when a row appears for the first time
  *   - "D" - when row disappears
  *   - "U" - whenever any row data has changed
  *
  * It relies on a user-specified primary key column to correlate the rows
  * between the two snapshots.
  *
  * If the data contains a column that is guaranteed to change whenever
  * any of the data columns changes (for example this can be a last
  * modification timestamp, an incremental version, or a data hash), then
  * it can be specified as modification indicator to speed up the detection of
  * modified rows.
  *
  * Schema Changes:
  *
  * This strategy will always preserve all columns from the existing and
  * new snapshots, so the set of columns can only grow.
  */
case class MergeStrategySnapshot(
  /** Names of the columns that uniquely identify the record throughout
    * its lifetime */
  primaryKey: Vector[String],
  /** Names of the columns to compared to determine if a row has changed between two snapshots.
    *
    * For example this can be a modification timestamp, an incremental
    * version, or a data hash. If not specified all data columns will be
    * compared one by one.
    */
  compareColumns: Vector[String] = Vector.empty
) extends MergeStrategyKind
