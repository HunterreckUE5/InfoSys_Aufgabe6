import org.apache.spark.sql.Dataset

trait Queries {

  def sundayPosts(posts: Dataset[Post]): Long
  def maxContributions(posts: Dataset[Post], comments: Dataset[Comment]): List[(String, Int)]
}