package com.evojam.play.search.elastic

import play.api.libs.json.{Json, Reads}

import org.elasticsearch.action.search.SearchResponse

package object searchops {

  implicit class SearchResponseOps(result: SearchResponse) {

    def collectHits[T: Reads]: List[T] =
      result.getHits.getHits.toList
        .map(_.source)
        .map(Json.parse)
        .map(_.as[T])
  }

}
