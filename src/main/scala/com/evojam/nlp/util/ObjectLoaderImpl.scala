package com.evojam.nlp.util

import java.io._
import java.util.zip.GZIPInputStream

import scala.util.control.Exception.catching

import com.google.inject.Inject

private[util] class ObjectLoaderImpl @Inject() () extends ObjectLoader {
  override def load[T](file: File, gzipped: Boolean) =
    (file.exists, gzipped) match {
      case (true, true) => loadGzip[T](new FileInputStream(file))
      case (true, false) => loadRaw[T](new FileInputStream(file))
      case _ => None
    }

  override def loadResource[T](name: String, gzipped: Boolean) =
    gzipped match {
      case true => loadGzip[T](resource(name))
      case false => loadRaw[T](resource(name))
    }

  private def loadGzip[T](is: InputStream): Option[T] = {
    val in = breeze.util.nonstupidObjectInputStream(
      new BufferedInputStream(
        new GZIPInputStream(is)))
    readObject[T](in)
  }

  private def loadRaw[T](is: InputStream): Option[T] = {
    val in = breeze.util.nonstupidObjectInputStream(
      new BufferedInputStream(is))
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

  private def resource(name: String): InputStream =
    getClass().getClassLoader().getResourceAsStream(name)
}
