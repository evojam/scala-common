package com.evojam.nlp.date

import org.joda.time.{Interval, DateTime}

trait DateParser {
  def parseDate(sentence: String): Option[DateTime]
  def parseDates(sentence: String): List[DateTime]
  def parseInterval(sentence: String): Option[Interval]
  def parseInterval(fromSentence: String, toSentence: String): Option[Interval]
}
