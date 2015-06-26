package com.evojam.play.search.elastic

import scala.collection.JavaConversions._
import scala.util.control.Exception.catching

import play.api.{Configuration, Play}

import org.elasticsearch.common.settings.ImmutableSettings

import com.google.inject.{Provides, Singleton}
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import net.codingwell.scalaguice.ScalaModule

import com.evojam.play.search.elastic.config.ElasticSearchConfig
import com.evojam.play.search.elastic.mock.MockElasticSearchClientImpl

object ElasticSearchClientModule extends ScalaModule {

  override def configure() =
    Play.current.configuration.getBoolean("elasticsearch.enabled").getOrElse(false) match {
      case true => bind[ElasticSearchClient].to[ElasticSearchClientImpl].in[Singleton]
      case false => bind[ElasticSearchClient].to[MockElasticSearchClientImpl].in[Singleton]
    }

  @Provides @Singleton
  def config(configuration: Configuration) =
    ElasticSearchConfig(
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

  def buildUri(config: ElasticSearchConfig) =
    ElasticsearchClientUri("elasticsearch://" + config.addresses.map {
      case (host, port) => s"$host:$port"
    }.mkString(","))

  @Provides @Singleton
  def client(config: ElasticSearchConfig) = ElasticClient.remote(buildUri(config))
}
