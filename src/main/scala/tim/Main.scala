package tim

import p752.tiles.Input
import p752.Engine
import p752.Tile
import p752.Event
import tim.files.Repo
import tim.app.Command
import p752.Style
import tim.tiles.SimpleShowTile
import scala.io.Source
import tim.util.Timing
import tim.util.SystemUtil
import tim.util.Util

object Main:
  given Conf = Conf
  private val errorStyle = Style(foreground = 196, bold = true)
  private val repo = new Repo()
  private val command = new Command()

  private def addRecord(
      time: Time,
      l: List[String],
      continueLast: Boolean = false
  ): Unit = {
    val text = l.mkString(" ")
    val issue = Util.extractIssue(text)
    val entry = Entry(
      time = time,
      message = Message(text),
      tag = None,
      issue = issue
    )
    val record = repo.loadRecord(Timing.currentDate())
    record match
      case None =>
        val record =
          Record(date = Timing.currentDate(), entries = entry :: Nil, None)
        repo.saveRecord(record)
      case Some(record) if record.eod.isEmpty =>
        val newEntries = if continueLast then
          val cont = record.entries.last.copy(time = Timing.currentTime())
          entry :: cont :: Nil
        else entry :: Nil
        val newRecord = record.copy(entries = record.entries ++ newEntries)
        repo.saveRecord(newRecord)
      case Some(_) =>
        println(errorStyle.render("Cannot add entry"))
        println("Current record has finished (it has a End of day record")

  }

  private def addEndRecord(): Unit = {
    val record = repo.loadRecord(Timing.currentDate())
    val message = record match
      case None => 
        "Nothing is added as a record...\nAborting"
      case Some(record)  =>
        record.eod match
          case None => 
            val newRecord = record.copy(eod = Some(Timing.currentTime()))
            repo.saveRecord(newRecord)
            "End added"
          case Some(value) =>
            val newRecord = record.copy(eod = Some(Timing.currentTime()))
            repo.saveRecord(newRecord)
            "End updated"
    println(message)
  }

  val help: String =
    s"""
    |Tim the time tracker
    |--------------------
    |* Run without any parameters to print today's timesheet:
    ||> t
    |
    |* Run without any commands to add an entry:
    ||> t Working on something
    |* Text could contain a at-sign to indicate an issue/project
    ||> t Working on @PRJ-125
    |
    |* -t [MIN]                 Record an entry with a time offset ([MIN] minutes ago)
    |* -tt [MIN]                Record an entry that started [MIN] minutes ago and finished just now and then continue the previous entry
    |* -e , --edit              Edit current day in default editor
    |* -e [N] , --edit [N]      Edit [N] days ago in default editor
    |* -E , --end               End current day at the current time
    |
    |Samples
    |----------
    ||> t -t 15 working on something else
    ||> t -tt 20 phone call
    ||> t -e 
    ||> t -e 3
    ||> t -E
    |
    |Commands
    |------------
    |* -c [PARAMS]             Commands
    |${command.help}
    |
    |* Use `--help` or `-h` to print manual
    |""".stripMargin
      .replaceAllLiterally("*", Style.empty.copy(foreground = 12).render("*"))
      .replaceAllLiterally("|>", Style.empty.copy(foreground = 82).render("|>"))

  def edit(n: Int): Unit = {
    val date = Timing.add(Timing.currentDate(), -1 * n)
    val path = repo.recordAddress(date)
    SystemUtil.openInEditor(path)
  }

  def isEditCommand(in: String): Boolean = in == "-e" || in == "--edit"

  def main(args: Array[String]): Unit =
    args.toList match {
      case Nil =>
        new SimpleShowTile(repo).render()
      case List(command) if isEditCommand(command) =>
        edit(0)
      case List(command, days)
          if isEditCommand(command) && Util.isNumber(days) =>
        edit(days.toInt)
      case List("-E") | List("--end") =>
        addEndRecord()
      case List("-h") | List("--help") =>
        println(help)
      case "-c" :: tail =>
        command.run(tail)
      case "-t" :: number :: tail if Util.isNumber(number) =>
        val time = Timing.add(Timing.currentTime(), -1 * number.toInt)
        addRecord(time, tail)
      case "-tt" :: number :: tail if Util.isNumber(number) =>
        val time = Timing.add(Timing.currentTime(), -1 * number.toInt)
        addRecord(time, tail, continueLast = true)
      case head :: tail if head.startsWith("-") =>
        val text = args.mkString(" ")
        println("Invalid command " + text)
        println(help)
      case params =>
        addRecord(Timing.currentTime(), params)
    }
