package org.mai.dep210.scala.stackoverflow

import scala.reflect.ClassTag

object Logic {

  private def filterByType[A <: Entity] (entities: Seq[Entity]) (implicit ev: ClassTag[A]) = entities collect {
    case t: A => t
  }

  private def joinById[A >:Null <: Entity] (primaryId: Int, entities: Seq[A]) : A = {
    primaryId match {
      case Int.MinValue => null
      case id => entities.find(_.id == id).get
    }
  }

  //obtain all comments from entities
  def getComments(entities: Seq[Entity]): Seq[Comment] = {
    filterByType[Comment](entities)
  }

  //split entities by type
  def splitEntities(entities: Seq[Entity]): (Seq[User], Seq[Post], Seq[Comment], Seq[Vote], Seq[Badge], Seq[Tag]) = {
    ( filterByType[User](entities),
      filterByType[Post](entities),
      filterByType[Comment](entities),
      filterByType[Vote](entities),
      filterByType[Badge](entities),
      filterByType[Tag](entities) )
  }

  //populate fields owner, lastEditor, tags with particular users from Seq[Post] and tags from Seq[Tag]
  def enreachPosts(posts: Seq[Post], users: Seq[User], tags: Seq[Tag]): Seq[EnreachedPost] = {
    posts.map {
      case post @ Post(_, _, _, _, _, _, _, ownerUserId, lastEditorUserId, _, _, _, _, tagNames)
        => EnreachedPost(
          post,
          joinById(ownerUserId, users),
          joinById(lastEditorUserId, users),
          tagNames.map {
            case "" => null
            case tagName => tags.find(_.tagName == tagName).get
          }
      )
      case _ => null
    }
  }

  //populate fields post and owner with particular post from Seq[Post] and user from Seq[User]
  def enreachComments(comments: Seq[Comment], posts: Seq[Post], users: Seq[User]): Seq[EnreachedComment] = {
    comments.map {
      case comment @ Comment(_, postId, _, _, _, userId)
      => EnreachedComment(
        comment,
        joinById(postId, posts),
        joinById(userId, users)
      )
    }
  }

  //find all links (like http://example.com/examplePage) in aboutMe field
  def findAllUserLinks(users: Seq[User]): Seq[(User, Seq[String])] = {
    val regex = """https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&\/\/=]*)""".r

    // Не отработает в случае если ссылка не отделена пробелами от текста, например, если сразу после ссылки запятая. Зато с паттерн матчингом
    //    Seq[(User, Seq[String])]()
    //      users.map(user => (user, user.about.split(" ").collect {
    //        case link @ regex(_, _) => link
    //      } .toSeq))

    // Без паттерн-матчинга, но, вроде как, всегда отработает
    users.map(user => (user, regex.findAllMatchIn(user.about).map(_.toString()).toList))
  }

  //find all users with the reputation bigger then reputationLimit with particular badge
  def findTopUsersByBadge(users: Seq[User], badges: Seq[Badge], badgeName: String, reputationLimit: Int): Seq[User] = {
    users.view.filter(u => u.reputation > reputationLimit) filter {
      u => badges.find(_.userId == u.id) match {
        case Some(badge) if badge.name == badgeName => true
        case _ => false
      }
    }
  }
}

case class EnreachedPost(
                        post: Post,
                        owner: User,
                        lastEditor: User,
                        tags: Seq[Tag]
                        )

case class EnreachedComment(
                          comment: Comment,
                          post: Post,
                          owner: User
                        )
