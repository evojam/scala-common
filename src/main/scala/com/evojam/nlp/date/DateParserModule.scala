package com.evojam.nlp.date

import play.api.Configuration

import com.google.inject.{Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import org.ocpsoft.prettytime.nlp.PrettyTimeParser

object DateParserModule extends ScalaModule {
  private val DatesSemiCRF = "date-interval-en-US.gz"

  override def configure() {
    bind[DateParser].to[DateParserImpl].in[Singleton]
  }

  @Provides @Singleton
  def prettyTimeParser() =
    new PrettyTimeParser()

  @Provides @Singleton
  def dateParserConfig(configuration: Configuration) =
    configuration.getString("nlp.dates-semicrf")
      .map(DateParserConfig(_, false))
      .getOrElse(DateParserConfig(DatesSemiCRF))
}
