package tim

import java.util.Random
import scala.collection.immutable.NumericRange.Inclusive

object TestUtil {

  val endedRecord: Record = Record(
    date = Date(2022, 11, 23),
    entries = Entry(Time(8, 40), Message("mes"), Some(Tag("tag")), None) ::
      Entry(
        Time(9, 50),
        Message("message2"),
        Some(Tag("tag2")),
        Some(Issue("issue1"))
      ) ::
      Entry(Time(9, 55), Message("message3"), Some(Tag("tag2")), None) ::
      Entry(Time(10, 50), Message("message2"), None, None) ::
      Nil,
    eod = Some(Time(22, 30))
  )

  val notEndedRecord: Record = Record(
    date = Date(2022, 11, 22),
    entries = Entry(Time(8, 40), Message("mes"), Some(Tag("tag")), None) ::
      Entry(
        Time(9, 50),
        Message("message2"),
        Some(Tag("tag2")),
        Some(Issue("issue1"))
      ) ::
      Entry(Time(9, 55), Message("message3"), Some(Tag("tag2")), None) ::
      Entry(Time(10, 50), Message("message2"), None, None) ::
      Nil,
    eod = None
  )
}

object TestConf extends Conf:
  override val basePath: String = "./temp/"

object Gen:
  val r: Random = new Random()

  val chars: Seq[Char] = 'a' to 'z'

  def nextInt(max: Int): Int = r.nextInt(max)

  def nextInt(gte: Int, lt: Int): Int =
    val diff = lt - gte
    gte + nextInt(diff)

  def option[T](f: () => T): Option[T] =
    if r.nextBoolean() then Some(f()) else None
  def seqOf[T](f: () => T): Seq[T] =
    val len = nextInt(0, 50)
    (0 to len).map(_ => f()).toSeq

  def oneOf[T](l: Seq[T]): T =
    l(nextInt(0, l.length))

  def nextText(): String = seqOf(() => oneOf(chars)).mkString

  def time(): Time = Time(nextInt(24), nextInt(60))
  def date(): Date = Date(nextInt(2030), nextInt(12), nextInt(31))
  def message(): Message = Message(nextText())
  def tag(): Tag = Tag(nextText())

  def entry(): Entry =
    Entry(time = time(), message = message(), tag = option(tag), None)

  def record(): Record = Record(date(), seqOf(entry).toList, option(time))
