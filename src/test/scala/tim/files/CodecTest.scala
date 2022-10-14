package tim.files

import Codecs.given
import org.junit.Assert._
import org.junit.Test
import tim.{Record, Date, Time, Message, Tag, Entry}
import tim.Gen

class CodecTest {

  @Test def composeTest(): Unit = {
    val c = Codec[Int].along[String].along[String]
    val input = 1 -> "" -> ""
    val raw = c.encode(input)
    val result = c.decode(raw)
    assertEquals(input, result)

  }

  @Test def recordTest(): Unit = {
    Gen.seqOf(Gen.record).toList.foreach { record =>
      val raw = Codec.encode(record)
      val result = Codec.decode[Record](raw)
      assertEquals(record, result)
    }
  }

  @Test def entryTest(): Unit = {
    val entries = Gen.seqOf(Gen.entry).toList
    val raw = Codec.encode(entries)
    val result = Codec.decode[List[Entry]](raw)
    assertEquals(entries, result)
  }
}
