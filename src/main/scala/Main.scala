object MainApp {
  def main(args: Array[String]): Unit = {

    val handler = new SparkHandler()

    val (comments, posts, users) = handler.importData()
    users.cache()
    println("\n--- RUNNING SPARK API VERSION ---")

    val sparkQueries = new SparkQueries()

    val sundayCountSpark = sparkQueries.sundayPosts(posts)
    println(s""""sunday_posts": $sundayCountSpark""")

    // B) Top User & Name
    val sparkResults = sparkQueries.maxContributions(posts, comments)

    if (sparkResults.nonEmpty) {
      val (userIdString, count) = sparkResults.head
      val userId = userIdString.toLong

      val name = sparkQueries.findUsername(userId, users)
      printUserResult(count, userId, name)
    }

    println("\n\n--- RUNNING SQL QUERY VERSION ---")

    val sqlQueries = new SQLQueries()


    val sundayCountSql = sqlQueries.sundayPosts(posts)
    println(s""""sunday_posts": $sundayCountSql""")


    val sqlResults = sqlQueries.maxContributions(posts, comments)

    if (sqlResults.nonEmpty) {
      val (userIdString, count) = sqlResults.head
      val userId = userIdString.toLong

      val name = sqlQueries.findUsername(userId, users)
      printUserResult(count, userId, name)
    }

    handler.close()
  }

  def printUserResult(count: Int, userId: Long, name: String): Unit = {
    println(s""""most_contributions": $count,""")
    println(s""""user_id": $userId,""")
    println(s""""name": "$name"""")
  }
}