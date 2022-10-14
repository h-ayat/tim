package tim.files

import tim.Conf
import tim.Tag
import tim.Entry
import tim.Record
import tim.Date
import tim.util.Timing

class Repo()(using conf: Conf):

  def addTag(tag: Tag): Unit =
    FileUtil.append(tag.value, conf.tagsPath)

  def loadTags(): List[Tag] =
    FileUtil
      .loadLines(conf.tagsPath)
      .map(_.trim)
      .filter(_.nonEmpty)
      .map(Tag.apply)

  def recordAddress(date: Date): String = {
    conf.entriesBasePath + date.pretty + ".csv"
  }
  def saveRecord(record: Record): Unit = {
    val raw = Codecs.encodeRecord(record)
    FileUtil.overwrite(raw, recordAddress(record.date))
  }

  def loadRecord(date: Date): Option[Record] =
    if FileUtil.exists(recordAddress(date)) then
      val raw = FileUtil.load(recordAddress(date))
      Some(Codecs.decodeRecord(raw))
    else None

  def loadAll(start: Date, daysBack: Int): Seq[Record] = {
    Timing.range(start, daysBack).map(loadRecord).flatten
  }
