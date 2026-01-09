import org.apache.spark.sql.{Dataset, SparkSession}

class SQLQueries (val session: SparkSession) extends Queries {

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

    resultDF.as[(Long, Long)]
      .collect()
      .map { case (id, count) => (id.toString, count.toInt) }
      .toList
  }


  def findUserName(posts: Dataset[Post], comments: Dataset[Comment], userId: Long): Option[String] = {

    posts.createOrReplaceTempView("posts")
    comments.createOrReplaceTempView("comments")

    val query =
      s"""
        SELECT userName
        FROM
          (
            SELECT userName FROM posts WHERE userId = '$userId'
            UNION
            SELECT userName FROM comments WHERE userId = '$userId'
          ) as combined
        WHERE userName IS NOT NULL
        LIMIT 1
      """

    val resultDF = session.sql(query)

    resultDF
      .as[String]
      .collect()
      .headOption
  }
}


  object SQLQueries {

    def apply(master: String = "local[*]", appName: String = "SQLQueries") = {
      val session = SparkSession
        .builder()
        .appName(appName)
        .master(master)
        .getOrCreate()

      new SQLQueries(session)
    }
  }
