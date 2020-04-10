/*
 * Copyright (c) 2018 kamu.dev
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.kamu.core.manifests.infra

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

import pureconfig.generic.auto._
import dev.kamu.core.manifests.parsing.pureconfig.yaml
import yaml.defaults._
import dev.kamu.core.manifests._
import org.apache.hadoop.fs.{FileSystem, Path}
import dev.kamu.core.utils.fs._
import pureconfig.{ConfigReader, ConfigWriter, Derivation}

import scala.reflect.ClassTag

class MetadataChainFS(fileSystem: FileSystem, datasetDir: Path) {

  def init(ds: DatasetSnapshot, systemTime: Instant): Unit = {
    val initialBlock = MetadataBlock(
      prevBlockHash = "",
      systemTime = systemTime,
      rootPollingSource = ds.rootPollingSource,
      derivativeSource = ds.derivativeSource
    ).hashed()

    val initialSummary = DatasetSummary(
      id = ds.id,
      kind = ds.kind,
      datasetDependencies = ds.dependsOn.toSet,
      vocabulary = ds.vocabulary,
      lastPulled = None,
      numRecords = 0,
      dataSize = 0
    )

    try {
      fileSystem.mkdirs(blocksDir)
      saveResource(initialSummary, summaryPath)
      saveResource(initialBlock, blocksDir.resolve(initialBlock.blockHash))
    } catch {
      case e: Exception =>
        fileSystem.delete(datasetDir, true)
        throw e
    }
  }

  // TODO: add invariant validation
  def append(_block: MetadataBlock): MetadataBlock = {
    val block = _block.hashed()
    saveResource(block, blocksDir.resolve(block.blockHash))
    block
  }

  def getSummary(): DatasetSummary = {
    loadResource[DatasetSummary](summaryPath)
  }

  def updateSummary(
    update: DatasetSummary => DatasetSummary
  ): DatasetSummary = {
    val newSummary = update(getSummary())
    saveResource(newSummary, summaryPath)
    newSummary
  }

  def getSnapshot(): DatasetSnapshot = {
    val summary = getSummary()

    val rootPollingSource =
      if (summary.kind == DatasetKind.Root)
        getBlocks()
          .find(_.rootPollingSource.isDefined)
          .map(_.rootPollingSource.get)
      else
        None

    val derivativeSource =
      if (summary.kind == DatasetKind.Derivative)
        getBlocks()
          .find(_.derivativeSource.isDefined)
          .map(_.derivativeSource.get)
      else
        None

    DatasetSnapshot(
      id = summary.id,
      rootPollingSource = rootPollingSource,
      derivativeSource = derivativeSource,
      vocabulary = summary.vocabulary
    )
  }

  def getBlocks(): Vector[MetadataBlock] = {
    val blocks = fileSystem
      .listStatus(blocksDir)
      .map(_.getPath)
      .map(loadResource[MetadataBlock])
      .map(b => (b.blockHash, b))
      .toMap

    val nextBlocks = blocks.values
      .map(b => (b.prevBlockHash, b.blockHash))
      .toMap

    val blocksOrdered =
      new scala.collection.immutable.VectorBuilder[MetadataBlock]()

    var parentBlockHash = ""
    while (nextBlocks.contains(parentBlockHash)) {
      parentBlockHash = nextBlocks(parentBlockHash)
      blocksOrdered += blocks(parentBlockHash)
    }

    blocksOrdered.result()
  }

  protected def summaryPath: Path = datasetDir.resolve("summary")

  protected def blocksDir: Path = datasetDir.resolve("blocks")

  /////////////////////////////////////////////////////////////////////////////
  // Helpers
  /////////////////////////////////////////////////////////////////////////////

  protected def saveResource[T <: Resource: ClassTag](obj: T, path: Path)(
    implicit derivation: Derivation[ConfigWriter[Manifest[T]]]
  ): Unit = {
    val outputStream = fileSystem.create(path)
    try {
      yaml.save(Manifest(obj), outputStream)
    } finally {
      outputStream.close()
    }
  }

  protected def loadResource[T <: Resource: ClassTag](path: Path)(
    implicit derivation: Derivation[ConfigReader[Manifest[T]]]
  ): T = {
    val inputStream = fileSystem.open(path)
    try {
      yaml.load[Manifest[T]](inputStream).content
    } finally {
      inputStream.close()
    }
  }

  protected implicit class MetadataBlockEx(b: MetadataBlock) {
    def hashed(): MetadataBlock = {
      val digest = MessageDigest.getInstance("sha-1")
      val repr = yaml.saveStr(b)

      val blockHash = digest
        .digest(repr.getBytes(StandardCharsets.UTF_8))
        .map("%02x".format(_))
        .mkString

      b.copy(blockHash = blockHash)
    }
  }

}
