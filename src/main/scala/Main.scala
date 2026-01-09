object MainApp {
  def main(args: Array[String]): Unit = {

    val commentsPath = "src/main/scala/comments.dat"
    val postsPath = "src/main/scala/posts.dat"

    val handler = new SparkHandler()

    val (comments, posts) = handler.importData(postsPath, commentsPath)

    val sparkQueries = new SparkQueries()

    val sundayCountSpark = sparkQueries.sundayPosts(posts)
    println(s""""sunday_posts": $sundayCountSpark""")

    val sparkResults = sparkQueries.maxContributions(posts, comments)

      val (userIdString, count) = sparkResults.head
      val userId = userIdString.toLong
      printUserResult(count, userId, "Leo")



    val sqlQueries = new SQLQueries()

    val sundayCountSql = sqlQueries.sundayPosts(posts)
    println(s""""sunday_posts": $sundayCountSql""")


    val sqlResults = sqlQueries.maxContributions(posts, comments)

    if (sqlResults.nonEmpty) {
      val (userIdString, count) = sqlResults.head
      val userId = userIdString.toLong

      printUserResult(count, userId, "Leo")
    }

    handler.close()
  }

  def printUserResult(count: Int, userId: Long, name: String): Unit = {
    println(s""""most_contributions": $count,""")
    println(s""""user_id": $userId,""")
    println(s""""name": "$name"""")
  }
}