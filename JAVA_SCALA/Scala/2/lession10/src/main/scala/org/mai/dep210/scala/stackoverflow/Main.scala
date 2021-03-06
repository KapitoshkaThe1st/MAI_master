package org.mai.dep210.scala.stackoverflow

object Main extends App {

  val loader = new DataLoader {
    override def basePath: String = "stackoverflow"
  }

  val entities = loader.loadData()

  val cmts = Logic.getComments(entities)
  cmts take(10) foreach println

  val (users, posts, comments, votes, badges, tags) = Logic.splitEntities(entities)

  println("-----------------------")
  val reachPosts = Logic.enreachPosts(posts, users, tags);
  reachPosts take(10) foreach println
  println("+++++++++++++++++++++++")

  val reachComments = Logic.enreachComments(comments, posts, users)
  reachComments take(10) foreach println

  val userLinks = Logic.findAllUserLinks(users)
  //  userLinks take(10) foreach(ul =>  println(ul._2))
  userLinks take(10) foreach println

  val topUsersByBadge = Logic.findTopUsersByBadge(users, badges, "Student", 100)
  topUsersByBadge take(10) foreach println


}
