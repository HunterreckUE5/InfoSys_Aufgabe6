import org.apache.spark.sql.{Dataset, SparkSession, functions}
import org.apache.spark.sql.functions._

class SparkQueries extends Queries {
  val session: SparkSession = SparkSession
    .builder()
    .appName("SQLQueries")
    .master("local[*]")
    .getOrCreate()

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

  /*
  def findUsername(userId: Long, users: Dataset[User]): String = {
    users
      .where($"id" === userId)  // Filter: Entspricht WHERE id = ...
      .select($"name")          // Projection: Entspricht SELECT name
      .as[String]               // Encoding: Dataset[Row] -> Dataset[String]
      .collect()                // Action: Daten zum Driver holen
      .headOption               // Safe access: Erstes Element holen (Option)
      .getOrElse("Unbekannt")   // Fallback, falls ID nicht existiert
  }*/
}