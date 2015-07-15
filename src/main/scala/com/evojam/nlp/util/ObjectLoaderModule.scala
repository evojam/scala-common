package com.evojam.nlp.util

import com.google.inject.Singleton
import net.codingwell.scalaguice.ScalaModule

object ObjectLoaderModule extends ScalaModule {
  override def configure() {
    bind[ObjectLoader].to[ObjectLoaderImpl].in[Singleton]
  }
}
