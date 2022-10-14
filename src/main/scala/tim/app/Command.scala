package tim.app

import tim.Conf
import tim.files.Repo
import tim.Tag

class Command()(using conf: Conf):
  val repo = new Repo()

  val help: String =
    """
    |Available commands:
    |-------------------
    |add-tag <TAG>        adds a tag
    |list-tags            prints list of all tags
    |""".stripMargin

  def run(l: List[String]): Unit = l match
    case "add-tag" :: tag :: Nil =>
      val tags = repo.loadTags()
      val newTag = Tag(tag)

      if tags.contains(newTag) then println("Duplicated tag\n")
      else repo.addTag(Tag(tag))
      printTags()

    case "list-tags" :: Nil =>
      printTags()

    case _ =>
      println("Unkown command sequence : " + l.mkString(" ") + "\n")
      println(help)
  def printTags(): Unit =
    println("Current tags:")
    println("-------------")
    repo.loadTags().map(_.value).foreach(println)
