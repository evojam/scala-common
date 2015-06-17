package com.evojam.play.search.elastic.config

case class ElasticSearchConfig(addresses: List[(String, Int)], clusterName: Option[String]) {
  require(addresses != null, "addresses cannot be null")
  require(clusterName != null, "clusterName cannot be null")
}
