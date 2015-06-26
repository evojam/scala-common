package com.evojam.play.search.elastic.mock

import scala.concurrent.Future.successful

import play.api.libs.json.Writes

import com.evojam.play.search.elastic.ElasticSearchClient

class MockElasticSearchClientImpl extends ElasticSearchClient {

  override def indexDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T) =
    successful(true)

  override def updateDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T) =
    successful(true)

  override def removeDocument(indexName: String, doctype: String, id: String) =
    successful(true)
}
