package tim.util

import tim.{Date, Time, Timespan}

import scala.scalanative.unsafe._

@extern
private object TimingNativeBinding {
  def get_now_str(): CString = extern

  def add_days_str(year: Int, month: Int, day: Int, days: Int): CString = extern

  def run_command(in: CString): Unit = extern

}

object Timing:
  import TimingNativeBinding.{get_now_str, add_days_str}

  private def cStrToDateTime(cstr: CString): (Date, Time) =
    val t2 = scala.scalanative.unsafe.fromCString(cstr)
    t2.split(" ").toList.map(_.toInt) match
      case year :: month :: day :: hour :: minute :: Nil =>
        Date(year, month, day) -> Time(hour, minute)

      case list =>
        throw new Exception(s"Could not parse $t2")

  def now(): (Date, Time) =
    cStrToDateTime(get_now_str())

  def currentTime(): Time = now()._2
  def currentDate(): Date = now()._1
  def diffMinutes(start: Time, end: Time): Int =
    val h = end.hour - start.hour
    val m = end.minute - start.minute
    (h * 60) + m

  def diff(start: Time, end: Time): Timespan =
    val mins = diffMinutes(start, end)
    minutesToTimespan(mins)

  def range(start: Date, daysBack: Int): Seq[Date] =
    (start :: (1 to daysBack).map(days => add(start, -1 * days)).toList).reverse

  def add(in: Date, days: Int): Date = {
    val cs = add_days_str(in.year, in.month, in.day, days)
    cStrToDateTime(cs)._1
  }

  def add(in: Time, minutes: Int): Time =
    val totalMinutes = (in.hour * 60) + in.minute + minutes
    Time(totalMinutes / 60, totalMinutes % 60)

  def minutesToTimespan(minutes: Int): Timespan =
    Timespan(minutes / 60, minutes % 60)

  def sum(ts: Seq[Timespan]): Timespan =
    val minutes = (ts.map(_.hour).sum * 60) + ts.map(_.minute).sum
    minutesToTimespan(minutes)
