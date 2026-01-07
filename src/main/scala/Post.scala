import java.sql.Timestamp
import java.time.OffsetDateTime

case class Post(
                 ts: Timestamp,
                 postId: Long,
                 userId: Long,
                 text: String,
                 userName: String
               ) {
  require(ts != null, s"Timestamp cant be null.")
  require(postId > 0, s"PostId cant be zero or less: $postId.")
  require(userId > 0, s"UserId cant be zero or less: $userId.")
  require(userName != null && userName.trim.nonEmpty, "Username cant be null")
}

object Post {
  def apply(line: String): Post = {
    val cols = line.split("\\|", -1)

    if (cols.length != 5) {
      throw new IllegalArgumentException(s"Wrong Column Count. Expected: 5, Real: ${cols.length}")
    }
    val timestamp  = Timestamp.from(OffsetDateTime.parse(cols(0)).toInstant)
    val postId = cols(1).toLong
    val userId = cols(2).toLong
    val text = cols(3)
    val userName = cols(4)

    new Post(timestamp, postId, userId, text, userName)
  }
}