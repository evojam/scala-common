package com.evojam.nlp.date

import java.io.{FileNotFoundException, File}

import scala.collection.JavaConversions._

import com.google.inject.Inject
import epic.sequences.SemiCRF
import org.joda.time.{Interval, DateTime}
import org.ocpsoft.prettytime.nlp.PrettyTimeParser

import com.evojam.nlp.ner.NamedEntityRecognizer
import com.evojam.nlp.util.ObjectLoader

private[nlp] class DateParserImpl @Inject() (
  prettyTimeParser: PrettyTimeParser,
  loader: ObjectLoader,
  ner: NamedEntityRecognizer,
  config: DateParserConfig) extends DateParser {

  private[this] lazy val datesCrf: SemiCRF[String, String] =
    (config.resource match {
      case true => loader.loadResource[SemiCRF[String, String]](config.datesSemiCrf)
      case false => loader.load[SemiCRF[String, String]](new File(config.datesSemiCrf))
    }).getOrElse(throw new FileNotFoundException(s"Unable to locate dates CRF: $config"))

  override def parseDate(sentence: String) =
    prettyTimeParser
      .parse(sentence)
      .headOption
      .map(new DateTime(_))

  override def parseDates(sentence: String) =
    prettyTimeParser
      .parse(sentence)
      .map(new DateTime(_))
      .toList

  override def parseInterval(sentence: String) =
    prettyTimeParser
      .parse(sentence)
      .headOption
      .map(date =>
        new Interval(
          new DateTime(date),
          addInterval(new DateTime(date), sentence)))

  override def parseInterval(fromSentence: String, toSentence: String) =
    for {
      from <- prettyTimeParser.parse(fromSentence).headOption
      to <- prettyTimeParser.parse(toSentence).headOption
    } yield new Interval(new DateTime(from), new DateTime(to))

  private def addInterval(startDate: DateTime, sentence: String): DateTime =
    periodFromSentence(sentence) match {
      case Year => startDate.plusYears(1)
      case Month => startDate.plusMonths(1)
      case Week => startDate.plusWeeks(1)
      case Day => startDate.plusDays(1)
      case UndefinedPeriod => startDate
    }

  private def periodFromSentence(sentence: String): Period =
    ner.tokenize(sentence.toLowerCase)(datesCrf)
      .foldLeft(UndefinedPeriod: Period) {
        case (shortest, (tag, _)) =>
          val period = periodFromTag(tag)
          (period.value < shortest.value) match {
            case true => period
            case false => shortest
          }
      }

  private def periodFromTag(tag: String): Period =
    tag match {
      case "YEAR" => Year
      case "MONTH" => Month
      case "WEEK" => Week
      case "DAY" => Day
      case _ => UndefinedPeriod
    }
}
