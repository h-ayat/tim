package tim.files

import java.io.File
import java.io.FileWriter

object FileUtil:

  val home = System.getProperty("user.home")

  def exists(path: String): Boolean = 
    new File(path).exists()

  def makePath(path: String): File =
    val f = new File(path)
    val p = if path.endsWith("/") then f else f.getParentFile()
    p.mkdirs()
    f

  def delete(path: String): Unit =
    val f = new File(path)
    f.mkdirs()
    if f.isDirectory() then f.listFiles.foreach(delete)
    delete(f)

  def delete(file: File): Unit =
    if file.exists() then file.delete()

  def load(path: String): String = loadLines(path).mkString("\n")

  def loadLines(path: String): List[String] =
    val f = new File(path)
    if f.exists() then scala.io.Source.fromFile(path).getLines().toList else Nil

  def overwrite(content: String, path: String): Unit =
    val f = new FileWriter(makePath(path))
    f.write(content)
    f.close()

  def append(content: String, path: String): Unit =
    val f = new FileWriter(makePath(path), true)
    f.append(content + "\n")
    f.close()
