package tim.app

import tim.Record
import p752.Engine
import p752.Tile
import p752.Event
import p752.tiles.Input

object ProcessDayApp {

  private val emptyInput = Input("")

  def run(record: Record): Unit = {
    val tile = ProcessDayApp(record, emptyInput)
  }
}

private case class ProcessDayApp(record: Record, input: Input)
    extends Tile[Record] {

  override def update(event: Either[Event, Record]): Tile[Record] = ???

  override val render: String = ???

}
