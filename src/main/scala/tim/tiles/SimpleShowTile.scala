package tim.tiles

import p752.Tiles
import tim.util.Timing
import tim.{Record}
import tim.files.Repo

class SimpleShowTile(repo: Repo) {
  def render(): Unit =
    val record = repo.loadRecord(Timing.currentDate())
    record.foreach(render)

  def render(record: Record): Unit = {
    val left = TimesheetTile(record)
    val tagTile = TagTile(record)
    val issueTile = IssueTile(record)
    val right = Tiles.renderVertical(tagTile.render , "\n \n", issueTile.render)
    val result = Tiles.renderHorizontal(left.render, "   ", right)
    println(result)
  }
}
