package tim.util

import tim.util.Timing
import tim.{Record, Time, Timespan}
import tim.Issue

object Util:

  def timeToString(time: Time): String =
    s"${time.hour}:${time.minute}"

  def timespanToString(time: Timespan): String =
    val pre = if time.hour > 0 then time.hour + "\" " else ""
    pre + time.minute + "'"

  def endOfDay(record: Record): Time =
    record.eod match
      case Some(value) => value
      case None =>
        if Timing.currentDate() == record.date then Timing.currentTime()
        else record.entries.last.time

  def timespans(record: Record): List[Timespan] =
    val ends = record.entries.tail.map(_.time) :+ endOfDay(record)
    record.entries.map(_.time).zip(ends).map(tu => Timing.diff(tu._1, tu._2))

  def extractIssue(text: String): Option[Issue] =
    text
      .replaceAll("[,.:/\\\\'\"]", " ")
      .replaceAll("  +", " ")
      .split(" ")
      .find(_.startsWith("@"))
      .map(_.filter(_ != '@'))
      .map(Issue.apply)

  def isNumber(in: String): Boolean = in.matches("\\d+")
