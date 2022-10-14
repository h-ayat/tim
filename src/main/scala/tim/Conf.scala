package tim

import tim.files.FileUtil

trait Conf:
  val basePath: String
  lazy val tagsPath: String = basePath + "tags.txt"
  lazy val entriesBasePath: String = basePath + "entries/"

object Conf extends Conf:
  private val env = System.getenv()
  private val home: String = {
    env.getOrDefault("TIM_BASE", FileUtil.home)
  }
  override val basePath = s"$home/.tim/"
