package tim

final case class Time(hour: Int, minute: Int)
final case class Timespan(hour: Int, minute: Int)
final case class Date(year: Int, month: Int, day: Int) {
  lazy val pretty = s"$year/$month/$day"
}

final case class Message(value: String) extends AnyVal
final case class Tag(value: String) extends AnyVal
final case class Issue(value: String) extends AnyVal

final case class Record(date: Date, entries: List[Entry], eod: Option[Time])
final case class Entry(
    time: Time,
    message: Message,
    tag: Option[Tag],
    issue: Option[Issue]
)
