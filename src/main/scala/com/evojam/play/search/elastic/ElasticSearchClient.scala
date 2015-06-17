package com.evojam.play.search.elastic

import scala.concurrent.Future

import play.api.libs.json.Writes

trait ElasticSearchClient {

  def indexDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T): Future[Boolean]

  def updateDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T): Future[Boolean]

  def removeDocument(indexName: String, doctype: String, id: String): Future[Boolean]

}
