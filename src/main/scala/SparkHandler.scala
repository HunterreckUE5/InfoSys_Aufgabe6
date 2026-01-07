import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.log4j.{Level, Logger}

class SparkHandler {

  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  val session: SparkSession = SparkSession
    .builder
    .appName("InfoSys")
    .master("local[*]")
    .getOrCreate()

  import session.implicits._

  def importData(): (Dataset[Comment], Dataset[Post], Dataset[User]) = {
    println("Starte Import...")

    val comments = importCommentsAndUsers("comments.dat")
    println(s"Comments geladen: ${comments.count()}")

    val posts = importPostsAndUsers("posts.dat")
    println(s"Posts geladen: ${posts.count()}")

    val user = importUsers("posts.dat", "comments.dat")
    println(s"Users geladen (Total): ${user.count()}")

    (comments, posts, user)
  }

  private def importPostsAndUsers(filePath: String): Dataset[Post] = {
    session.read.textFile(filePath).flatMap { line =>
      Some(Post(line))
    }
  }

  private def importCommentsAndUsers(filePath: String): Dataset[Comment] = {
    session.read.textFile(filePath).flatMap { line =>
      Some(Comment(line))
    }
  }

  private def importUsers(pathPosts: String, pathComments: String): Dataset[User] = {


    val usersFromPosts = session.read.textFile(pathPosts).flatMap { line =>
        Some(User(line))
    }

    val usersFromComments = session.read.textFile(pathComments).flatMap { line =>
      try {
        Some(User(line))
      } catch {
        case _: Exception => None
      }
    }

    usersFromPosts
      .union(usersFromComments)
      .dropDuplicates("id")
  }

  def close(): Unit = {
    session.stop()
  }
}