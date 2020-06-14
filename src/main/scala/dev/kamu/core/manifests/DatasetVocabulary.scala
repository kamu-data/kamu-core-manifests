/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests

case class DatasetVocabulary(
  systemTimeColumn: Option[String] = None,
  eventTimeColumn: Option[String] = None
) extends Resource {
  def withDefaults(): DatasetVocabulary = {
    copy(
      systemTimeColumn = systemTimeColumn.orElse(Some("system_time")),
      eventTimeColumn = eventTimeColumn.orElse(Some("event_time"))
    )
  }
}
