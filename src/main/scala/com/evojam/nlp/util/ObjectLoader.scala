package com.evojam.nlp.util

import java.io.File

trait ObjectLoader {
  def load[T](file: File, gzipped: Boolean = true): Option[T]
}
