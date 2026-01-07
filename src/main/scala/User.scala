case class User(
                 id: Long,
                 name: String
               ) {
  require(id > 0)
  require(name != null)
}

object User {
  def apply(line: String): User = {
    val cols = line.split("\\|", -1)

    val id = cols(2).toLong
    val name = cols(4).toString

    return new User(id, name);
  }
}