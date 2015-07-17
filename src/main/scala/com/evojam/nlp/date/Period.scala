package com.evojam.nlp.date

import org.joda.time.DateTimeConstants._
import org.joda.time.{Period => JodaPeriod, Months, Years}

private[date] abstract sealed class Period(val value: Long)

private[date] case object UndefinedPeriod extends Period(Long.MaxValue)

private[date] case object Year extends Period(new JodaPeriod(Years.ONE).getMillis)

private[date] case object Month extends Period(new JodaPeriod(Months.ONE).getMillis)

private[date] case object Week extends Period(MILLIS_PER_WEEK)

private[date] case object Day extends Period(MILLIS_PER_DAY)
