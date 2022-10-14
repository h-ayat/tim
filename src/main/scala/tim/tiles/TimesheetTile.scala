package tim.tiles

import p752.tiles.Table
import tim.util.Util
import tim.{Issue, Message, Record, Tag, Time, Timespan}

object TimesheetTile:
  case class Entry(
      start: Time,
      message: Message,
      tag: Option[Tag],
      issue: Option[Issue],
      diff: Timespan
  )


  def apply(record: Record): Table[Entry] = {
    new Table[Entry](
      headers,
      raw = make(record),
      show = show,
      Some("Entries"),
    )
  }

  private def show(entry: Entry): List[String] =
    import entry._
    Util.timeToString(start) ::
      message.value ::
      tag.map(_.value).getOrElse("-") ::
      issue.map(_.value).getOrElse("-") ::
      Util.timespanToString(diff) ::
      Nil
  private def make(record: Record): List[Entry] =
    val timespans = Util.timespans(record)
    record.entries.zip(timespans).map { case entry -> diff =>
      Entry(entry.time, entry.message, entry.tag, entry.issue, diff)
    }

  private val headers = "Start,Message,Tag,Issue,Duration".split(",").toList
