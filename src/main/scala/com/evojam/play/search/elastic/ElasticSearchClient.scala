package com.evojam.play.search.elastic

import scala.concurrent.Future

import play.api.libs.json.Writes

import com.sksamuel.elastic4s.SearchDefinition
import org.elasticsearch.action.search.SearchResponse

trait ElasticSearchClient {
  def search(searchDef: SearchDefinition): Future[SearchResponse]

  def indexDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T): Future[Boolean]

  def updateDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T): Future[Boolean]

  def removeDocument(indexName: String, doctype: String, id: String): Future[Boolean]
}
