package tim.util

import scala.scalanative.unsafe._

@extern
private object SystemNativeBinding {
  def run_command(in: CString): Unit = extern
}

object SystemUtil {
  private val env = System.getenv()

  def runCommand(str: String): Unit = {
    val cstring = toCString(str)(Zone.open())
    SystemNativeBinding.run_command(cstring)
  }

  def openInEditor(path: String): Unit = {
    val editor = env.getOrDefault("EDITOR", "vim")
    val command = s"$editor $path"
    runCommand(command)
  }
}
