package com.evojam.nlp.ner

import com.google.inject.Singleton
import net.codingwell.scalaguice.ScalaModule

object NamedEntityRecognizerModule extends ScalaModule {
  override def configure() {
    bind[NamedEntityRecognizer].to[NamedEntityRecognizerImpl].in[Singleton]
  }
}
