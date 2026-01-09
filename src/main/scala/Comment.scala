import java.sql.Timestamp
import java.time.OffsetDateTime

case class Comment(
                    ts: Timestamp,
                    commentId: Long,
                    userId: Long,
                    commentText: String,
                    userName: String,
                    postCommented: Long,
                    commentReplied: Long
                  ) {
  require(commentId > 0, "CommentId cant be less than 1")
  require(userId > 0, "UserId has to be postive")
  require(userName != null, "userName cant be null")
}

object Comment {
  def apply(line: String): Comment = {
    val cols = line.split("\\|", -1)

    if (cols.length != 7) {
      throw new IllegalArgumentException(s"Falsche Spaltenanzahl. Erwartet: 7, Gefunden: ${cols.length}")
    }
    val timestamp = Timestamp.from(OffsetDateTime.parse(cols(0)).toInstant)
    val commentId = cols(1).toLong
    val userId = cols(2).toLong
    val commentText = cols(3)
    val userName = cols(4)
    val postCommented = cols(5).toLongOption.getOrElse(-1L)
    val commentReplied = cols(6).toLongOption.getOrElse(-1L)

    new Comment(timestamp, commentId, userId, commentText, userName, postCommented, commentReplied)
  }
}

