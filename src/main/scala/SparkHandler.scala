import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.log4j.{Level, Logger}

class SparkHandler(val session : SparkSession) {

  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  import session.implicits._

  def importData(pathToPosts : String, pathToComments: String): (Dataset[Comment], Dataset[Post]) = {
    println("Starte Import...")

    val comments = importComments(pathToComments)
    println(s"Comments geladen: ${comments.count()}")

    val posts = importPosts(pathToPosts)
    println(s"Posts geladen: ${posts.count()}")

    (comments, posts)
  }

  private def importPosts(filePath: String): Dataset[Post] = {
    session.read.textFile(filePath).flatMap { line =>
      Some(Post(line))
    }
  }

  private def importComments(filePath: String): Dataset[Comment] = {
    session.read.textFile(filePath).flatMap { line =>
      Some(Comment(line))
    }
  }

  def close(): Unit = {
    session.stop()
  }
}
object SparkHandler {

  def apply(appName: String = "InfoSys", master: String = "local[*]"): SparkHandler = {

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    val session = SparkSession
      .builder
      .appName(appName)
      .master(master)
      .config("spark.sql.session.timeZone", "UTC")
      .getOrCreate()

    new SparkHandler(session)
  }
}