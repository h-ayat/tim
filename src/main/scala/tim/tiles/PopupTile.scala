package tim.tiles

import p752.Tile
import p752.Event
import p752.Style
import p752.tiles.HorizontalList
import p752.Tiles
import Tiles.{dimension, pureSize}
import p752.Align
import p752.Padding
import p752.Border

object PopupTile:
  private val pink = 161
  private val white = 231
  private val selectedStyle =
    Style.apply(foreground = white, background = pink, bold = true)

  def apply[T](message: String, options: List[T], show: T => String) = {
    val list = new HorizontalList[T](
      options,
      show,
      HorizontalList.Spacing.Separator("  "),
      selectedStyle = selectedStyle,
      index = 0
    )

  }

case class PopupTile[T](
    message: String,
    list: HorizontalList[T],
    result: Option[T]
) extends Tile[Any] {

  private val messageParts = message.split("\n").toList
  private val padding = Padding(1, 1, 1, 1)
  private val border = Border(Style.empty.copy(foreground = PopupTile.pink))

  override def update(event: Either[Event, Any]): Tile[Any] =
    (result, event) match
      case None -> Left(Event.Special.Enter) =>
        this.copy(result = Some(list.selected))
      case None -> _ =>
        this.copy(list = list.update(event))
      case Some(value) -> _ =>
        this

  override val render: String =
    val parts = messageParts :+ "" :+ list.render
    val content = Tiles.renderHorizontal(Align.Horizontal.Center, parts: _*)
    border.render(padding.render(content))

}
