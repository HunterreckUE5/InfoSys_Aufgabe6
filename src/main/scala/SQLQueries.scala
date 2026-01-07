import org.apache.spark.sql.{Dataset, SparkSession}

class SQLQueries extends Queries {

  val session: SparkSession = SparkSession
    .builder()
    .appName("SQLQueries")
    .master("local[*]")
    .getOrCreate()

  import session.implicits._

  override def sundayPosts(posts: Dataset[Post]): Long = {
    posts.createOrReplaceTempView("posts")

    val resultDF = session.sql("""
      SELECT * FROM posts
      WHERE dayofweek(ts) = 1
    """)
    resultDF.count()
  }

  override def maxContributions(posts: Dataset[Post], comments: Dataset[Comment]): List[(String, Int)] = {

    posts.createOrReplaceTempView("posts")
    comments.createOrReplaceTempView("comments")

    val query =
      """
       SELECT userId, count(*) as c
        | FROM
        | (Select userId from posts
        | UNION ALL
        | SELECT userId from comments) as allTogether
        | Group By userId
        | ORDER BY c DESC
        | Limit 1
        |""".stripMargin

    val resultDF = session.sql(query)

    resultDF.show() // Zur Kontrolle auf der Konsole

    resultDF.as[(Long, Long)]
      .collect()
      .map { case (id, count) => (id.toString, count.toInt) }
      .toList
  }

  def findUsername(userId: Long, users: Dataset[User]): String = {
    users.createOrReplaceTempView("users")

    // Wir nutzen String Interpolation (s"..."), um die ID direkt in das SQL einzufügen
    val query = s"SELECT name FROM users WHERE Id = $userId"

    val resultDF = session.sql(query)

    // Wir erwarten einen String zurück (den Namen)
    // headOption verhindert einen Absturz, falls die ID nicht gefunden wird
    resultDF.as[String].collect().headOption.getOrElse("Unbekannt")
  }
}