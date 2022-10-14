package tim.files

import tim.{Date, Entry, Issue, Message, Record, Tag, Time}

trait Codec[T]:
  def decode(in: String): T
  def encode(t: T): String

object Codec:
  def apply[T: Codec]: Codec[T] = summon[Codec[T]]

  extension [A](c: Codec[A])
    def along[B: Codec] = compose[A, B]()(using c, summon[Codec[B]])
    def byMap[B](a2b: A => B, b2a: B => A): Codec[B] =
      Codec.byMap(a2b, b2a)(using c)

  def compose[A: Codec, B: Codec](): Codec[(A, B)] =
    val left = summon[Codec[A]]
    val right = summon[Codec[B]]
    new Codec[(A, B)] {

      override def decode(in: String): (A, B) =
        val data = in.split(",", -1).toList
        left.decode(data.init.mkString(",")) -> right.decode(data.last)

      override def encode(t: (A, B)): String =
        val (a, b) = t
        left.encode(a) + "," + right.encode(b)

    }

  def byMap[A: Codec, B](a2b: A => B, b2a: B => A): Codec[B] =
    val codec = summon[Codec[A]]
    new Codec[B] {

      override def decode(in: String): B = a2b(codec.decode(in))

      override def encode(t: B): String = codec.encode(b2a(t))
    }

  def encode[T: Codec](t: T): String =
    val c = summon[Codec[T]]
    c.encode(t)

  def decode[T: Codec](s: String): T =
    val c = summon[Codec[T]]
    c.decode(s)

end Codec

object Codecs:
  given Codec[String] = new Codec[String] {
    val PLACEHOLDER = "\\comma\\"
    override def decode(in: String): String =
      in.replaceAllLiterally(PLACEHOLDER, ",").replaceAllLiterally("\n", " ")
    override def encode(t: String): String =
      t.replaceAllLiterally(",", PLACEHOLDER)
  }

  given Codec[Int] = new Codec[Int] {
    override def decode(in: String): Int = in.toInt

    override def encode(t: Int): String = t.toString
  }

  given optionCodec[T: Codec]: Codec[Option[T]] = new Codec[Option[T]]:
    override def decode(in: String): Option[T] =
      if in == "" then None else Some(summon[Codec[T]].decode(in))
    override def encode(t: Option[T]): String = t match {
      case None        => ""
      case Some(value) => summon[Codec[T]].encode(value)
    }

  given listCodec[T: Codec]: Codec[List[T]] = new Codec[List[T]]:
    override def decode(in: String): List[T] =
      val c = summon[Codec[T]]
      in.split("\n").toList.map(c.decode)

    override def encode(t: List[T]): String =
      val c = summon[Codec[T]]
      t.map(c.encode).mkString("\n")

  given Codec[Tag] = new Codec[Tag]:
    override def encode(t: Tag): String = t.value
    override def decode(in: String): Tag = Tag(in)

  given Codec[Message] = summon[Codec[String]].byMap(Message.apply, _.value)
  given Codec[Issue] = summon[Codec[String]].byMap(Issue.apply, _.value)

  given Codec[Date] = new Codec[Date]:
    override def decode(in: String): Date =
      val parts = in.split("/")
      Date(parts(0).toInt, parts(1).toInt, parts(2).toInt)

    override def encode(t: Date): String =
      import t.{year, month, day}
      s"$year/$month/$day"

  given Codec[Time] = new Codec[Time]:
    override def decode(in: String): Time =
      val parts = in.split(":")
      Time(parts(0).toInt, parts(1).toInt)

    override def encode(t: Time): String =
      import t.{hour, minute}
      s"$hour:$minute"

  given Codec[Entry] =
    summon[Codec[Time]]
      .along[Message]
      .along[Option[Tag]]
      .along[Option[Issue]]
      .byMap[Entry](
        { case time -> message -> tag -> issue =>
          Entry(time, message, tag, issue)
        },
        x => {
          x.time -> x.message -> x.tag -> x.issue
        }
      )

  given Codec[Record] = new Codec[Record]:
    val header = Codec[Date].along[Option[Time]]
    override def encode(t: Record): String =
      header.encode(t.date, t.eod) + "\n" + Codec.encode(t.entries)

    override def decode(in: String): Record =
      val data = in.split("\n").toList
      val (date, eod) = header.decode(data.head)
      Record(
        date,
        data.tail.map(Codec.decode[Entry]),
        eod
      )

  def encodeRecord(rec: Record): String = Codec.encode(rec)
  def decodeRecord(in: String): Record = Codec.decode[Record](in)
end Codecs
