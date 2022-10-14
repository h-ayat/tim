package tim.tiles

import tim.util.Timing
import tim.{Record, Timespan}
import tim.util.Util
import p752.tiles.Table

object IssueTile:
  case class Entry(issue: String, diff: Timespan)

  def apply(record: Record): Table[Entry] =
    new Table[Entry](
      headers = headers,
      raw = make(record),
      show = show,
      title = Some("Issues")
    )


  private def show(entry: Entry): List[String] =
    entry.issue ::
      Util.timespanToString(entry.diff) ::
      Nil

  private def make(record: Record): List[Entry] =
    val ts = Util.timespans(record)
    record.entries
      .zip(ts)
      .map { case entry -> diff =>
        entry.issue -> diff
      }
      .groupBy(_._1)
      .toList
      .map { case maybeIssue -> objects =>
        Entry(
          maybeIssue.map(_.value).getOrElse("-UNNAMED-"),
          Timing.sum(objects.map(_._2))
        )
      }
  end make

  private val headers = "Issue,Duration".split(",").toList
