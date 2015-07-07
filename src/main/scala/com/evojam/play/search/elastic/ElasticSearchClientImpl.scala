package com.evojam.play.search.elastic

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.language.implicitConversions

import play.api.Logger
import play.api.libs.json._

import com.google.inject.Inject
import com.sksamuel.elastic4s.{SearchDefinition, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.DocumentSource
import org.elasticsearch.action.ActionResponse

private final class ElasticSearchClientImpl @Inject() (elastic: ElasticClient) extends ElasticSearchClient {
  require(elastic != null, "elastic cannot be null")

  val logger = Logger(getClass)

  case class JsValueSource(js: JsValue) extends DocumentSource {
    override def json = js.toString()
  }

  implicit def toSource[T <: JsValue](js: T): DocumentSource = JsValueSource(js)

  private def getHeaders[R <: ActionResponse](response: R) =
    Option(response.getHeaders)
      .map(_.toString)
      .getOrElse("<null>")

  private def executeInsert(indexName: String, doctype: String, id: String, in: DocumentSource) =
    elastic.execute {
      logger.debug(s"index into index=$indexName type=$doctype doc=${in.json}")
      index.into(indexName -> doctype)
        .doc(in)
        .id(id)
    }

  override def search(searchDef: SearchDefinition) =
    elastic.execute(searchDef)

  override def indexDocument[T](indexName: String, doctype: String, id: String, doc: T)(implicit w: Writes[T]) = {
    require(indexName != null, "indexName cannot be null")
    require(doctype != null, "doctype cannot be null")
    require(doc != null, "doc cannot be null")
    require(w != null, "w cannot be null")

    w.writes(doc) match {
      case json: JsObject =>
        executeInsert(indexName, doctype, id, json).map(Option(_))
          .map {
          case Some(response) if response.isCreated =>
            logger.info("Document has been indexed (create), retrieving headers= " + getHeaders(response))
            true
          case Some(response) if !response.isCreated =>
            logger.info("Document has been indexed (update), retrieving headers= " + getHeaders(response))
            true
          case None =>
            logger.error("Elastic execute result IndexResponse is null, I treat it as an error but can do nothing")
            false
        }
      case _ =>
        logger.error("Refuse to index document that is not a JsObject")
        successful(false)
    }
  }

  private def executeUpdate(indexName: String, doctype: String, id: String, in: DocumentSource) =
    elastic.execute {
      logger.debug(s"Update document index=$indexName type=$doctype id=$id doc=${in.json}")
      update
        .id(id)
        .in(indexName -> doctype)
        .doc(in)
    }

  override def updateDocument[T](indexName: String, doctype: String, id: String, in: T)(implicit w: Writes[T]) = {
    require(indexName != null, "indexName cannot be null")
    require(doctype != null, "doctype cannot be null")
    require(in != null, "in cannot be null")
    require(w != null, "w cannot be null")

    w.writes(in) match {
      case json: JsObject =>
        executeUpdate(indexName, doctype, id, json).map(Option(_))
          .map {
          case Some(response) if response.isCreated =>
            logger.info("Document has been updated, retrieving headers=" + getHeaders(response))
            true
          case Some(response) =>
            logger.info("Document has been updated, retrieving headers=" + getHeaders(response))
            true
          case None =>
            logger.error("Elastic execute update result IndexResponse is null, " +
                         "Treating it as an error but can do nothing")
            false
        }
      case _ =>
        logger.error("Refuse to update document that is not a JsObject")
        successful(false)
    }
  }

  private def executeRemove(indexName: String, doctype: String, id: String) =
    elastic.execute {
      logger.debug(s"Remove document from index=$indexName type=$doctype id=$id")
      delete
        .id(id)
        .from(indexName -> doctype)
    }

  override def removeDocument(indexName: String, doctype: String, id: String) =
    executeRemove(indexName, doctype, id).map {
      case ok if ok.isFound => true
      case _ =>
        logger.warn(s"Document in index=$indexName type=$doctype id=$id not found")
        false
    }
}
