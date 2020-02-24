/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

sealed trait ProcessingStepKind {}

object ProcessingStepKind {

  case class SparkSQL(
    /** An alias given to the result of this step that can be used to referred to it in the later steps */
    alias: Option[String] = None,
    /** An SQL statement **/
    query: String
  ) extends ProcessingStepKind

}
