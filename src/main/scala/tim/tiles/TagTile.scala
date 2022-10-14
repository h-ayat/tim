package tim.tiles

import p752.tiles.Table
import tim.util.Util
import tim.util.Timing
import tim.{Record, Tag, Timespan}

object TagTile:
  case class Entry(tag: String, diff: Timespan)

  def apply(record: Record): Table[Entry] =
    new Table[Entry](
      headers = headers,
      raw = make(record),
      show = show,
      title = Some("Tags"),
    )


  private def show(tagTileEntry: Entry): List[String] =
    tagTileEntry.tag ::
      Util.timespanToString(tagTileEntry.diff) ::
      Nil

  private def make(record: Record): List[Entry] =
    val ts = Util.timespans(record)
    record.entries
      .zip(ts)
      .map { case entry -> diff =>
        entry.tag -> diff
      }
      .groupBy(_._1)
      .toList
      .map { case maybeTag -> objects =>
        Entry(
          maybeTag.map(_.value).getOrElse("-UNTAGGED-"),
          Timing.sum(objects.map(_._2))
        )
      }

  private val headers = "Tag,Duration".split(",").toList