package com.evojam.play.search.elastic

import scala.collection.JavaConversions._
import scala.util.control.Exception.catching

import play.api.Configuration

import org.elasticsearch.common.settings.ImmutableSettings

import com.google.inject.Provides
import com.google.inject.Singleton
import com.sksamuel.elastic4s.ElasticClient
import net.codingwell.scalaguice.ScalaModule

import com.evojam.play.search.elastic.config.ElasticSearchConfig

class ElasticSearchClientModule extends ScalaModule {

  override def configure() =
    bind[ElasticSearchClient].to[ElasticSearchClientImpl].in[Singleton]

  @Provides @Singleton
  def config(configuration: Configuration) = ElasticSearchConfig(
    configuration.getStringList("elasticsearch.addresses")
      .map(_.toList)
      .getOrElse(Nil)
      .map(_.split(":"))
      .collect {
      case Array(host, port) if host.nonEmpty && port.nonEmpty =>
        host -> catching(classOf[NumberFormatException]).opt(port.toInt)
    }.collect {
      case (host, Some(port)) => host -> port
    },
    configuration.getString("elasticsearch.clustername"))

  def settings(config: ElasticSearchConfig) = {
    val builder = ImmutableSettings.builder()

    config.clusterName
      .map(builder.put("cluster.name", _))
      .getOrElse(Unit)

    builder.build()
  }

  @Provides @Singleton
  def client(config: ElasticSearchConfig) = ElasticClient.remote(config.addresses: _*)
}
