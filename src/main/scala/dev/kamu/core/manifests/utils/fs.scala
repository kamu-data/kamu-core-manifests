package dev.kamu.core.manifests.utils

import java.util.zip.ZipInputStream

import org.apache.hadoop.fs.{FileSystem, Path}

package object fs {

  implicit class PathExt(val p: Path) {
    def resolve(child: String): Path = {
      new Path(p, child)
    }

    def resolve(child: Path): Path = {
      new Path(p, child)
    }
  }

  implicit class FileSystemExt(val fs: FileSystem) {
    def toAbsolute(p: Path): Path = {
      if (p.isAbsolute)
        p
      else
        fs.getWorkingDirectory.resolve(p)
    }
  }

}
