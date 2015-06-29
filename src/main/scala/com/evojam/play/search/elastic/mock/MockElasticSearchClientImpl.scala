package com.evojam.play.search.elastic.mock

import scala.concurrent.Future.{successful, failed}

import play.api.libs.json.Writes

import com.evojam.play.search.elastic.ElasticSearchClient
import com.sksamuel.elastic4s.SearchDefinition

class MockElasticSearchClientImpl extends ElasticSearchClient {
  override def search(searchDef: SearchDefinition)
    = failed(new UnsupportedOperationException("Not able to search on mock client."))

  override def indexDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T) =
    successful(true)

  override def updateDocument[T: Writes](indexName: String, doctype: String, id: String, doc: T) =
    successful(true)

  override def removeDocument(indexName: String, doctype: String, id: String) =
    successful(true)
}
