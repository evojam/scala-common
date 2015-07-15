package com.evojam.nlp.util

import java.io._
import java.util.zip.GZIPInputStream

import scala.util.control.Exception.catching

import com.google.inject.Inject

private[util] class ObjectLoaderImpl @Inject() () extends ObjectLoader {
  override def load[T](file: File, gzipped: Boolean) =
    (file.exists, gzipped) match {
      case (true, true) => loadGzip[T](file)
      case (true, false) => loadRaw[T](file)
      case _ => None
    }

  private def loadGzip[T](file: File): Option[T] = {
    val in = breeze.util.nonstupidObjectInputStream(
      new BufferedInputStream(
        new GZIPInputStream(
          new FileInputStream(file))))
    readObject[T](in)
  }

  private def loadRaw[T](file: File): Option[T] = {
    val in = breeze.util.nonstupidObjectInputStream(
      new BufferedInputStream(
        new FileInputStream(file)))
    readObject[T](in)
  }

  private def readObject[T](in: ObjectInputStream): Option[T] =
    catching(classOf[IOException]).opt {
      try {
        in.readObject().asInstanceOf[T]
      } finally {
        in.close()
      }
    }
}
