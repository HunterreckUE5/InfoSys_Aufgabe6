import org.apache.spark.sql.{Dataset, SparkSession, functions}
import org.apache.spark.sql.functions._

class SparkQueries(val session: SparkSession) extends Queries {

  import session.implicits._

  override def sundayPosts(posts: Dataset[Post]): Long = {

    posts.createOrReplaceTempView("posts")
    return posts.where("dayofweek(ts) = 1")
      .count()

  }

  override def maxContributions(posts: Dataset[Post], comments: Dataset[Comment]): List[(String, Int)] = {

    val postUsers = posts.select("userId")
    val commentUsers = comments.select("userId")

    val contributions = postUsers.union(commentUsers)
      .groupBy("userId")
      .count()
      .orderBy(desc("count"))

    contributions
      .select(col("userId"), col("count").cast("int"))
      .as[(String, Int)]
      .collect()
      .toList
  }

  def findUserName(posts: Dataset[Post], comments: Dataset[Comment], userId: Long): Option[String] = {

    val distinctNamesPosts = posts
      .filter(col("userId") === userId)
      .select(col("userName"))
      .where(col("userName").isNotNull)


    val distinctNamesComments = comments
      .filter(col("userId") === userId)
      .select(col("userName"))
      .where(col("userName").isNotNull)

    distinctNamesPosts
      .union(distinctNamesComments)
      .limit(1)
      .as[String]
      .collect()
      .headOption
  }

}

object SparkQueries {

  def apply(master: String = "local[*]", appName: String = "SQLQueries"): SparkQueries = {

    val session = SparkSession
      .builder()
      .appName(appName)
      .master(master)
      .getOrCreate()

    new SparkQueries(session)
  }
}