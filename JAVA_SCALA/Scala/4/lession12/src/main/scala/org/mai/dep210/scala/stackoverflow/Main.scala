package org.mai.dep210.scala.stackoverflow

import org.log4s._
import scopt.OptionParser
import scalikejdbc._
import scalikejdbc.config._

import java.io.File
import com.github.tototoshi.csv._

object Main extends App {
  /*
  StackOverflowLoader 1.0
  Usage: StackOverflowLoader [load|clean|init|extract]

  Command: load [options]
  Load - это команда загрузки данных из файлов
    --path <value>           Путь к папке с файлами
    -a, --append             Не удалять данные при загрузке. По умолчанию данные будут перезатираться
  Command: clean [options]
  Удалить данные из базы данных
    -dt, --drop_tables        Удалить таблицы
  Command: init [options]
  Создать таблицы
    -f, --force              Пересоздать таблицы, если существуют
  Command: extract [options]
  Выгрузить данные в csv формате
    -q, --query <value>             Запрос на выбор данных
    --file <value>                  Файл, куда выгрузятся данные
  */

  private[this] val logger = getLogger

  object Commands extends Enumeration {
    type Command = Value
    val NoCommand, Load, Clean, Init, Extract = Value
  }

  case class Config(
                     command: Commands.Command = Commands.NoCommand,
                     path: String = "",
                     file: String = "",
                     append: Boolean = false,
                     dropTables: Boolean = false,
                     force: Boolean = false,
                     query: String = ""
                   )

  val parser = new OptionParser[Config]("StackOverflowLoader") {
    head("StackOverflowLoader", "1.0")
    cmd("load")
      .action((_, c) => c.copy(command = Commands.Load))
      .text("Load - это команда загрузки данных из файлов")
      .children(
        opt[String]("path")
          .required()
          .valueName("<path>")
          .action((p, c) => c.copy(path = p))
          .text("Путь к папке с файлами"),
        opt[Unit]("append")
          .abbr("a")
          .action((_, c) => c.copy(append = true))
          .text("Не удалять данные при загрузке. По умолчанию данные будут перезатираться")
      )
    cmd("clean")
      .action((_, c) => c.copy(command = Commands.Clean))
      .text("Удалить данные из базы данных")
      .children(
        opt[Unit]("drop_tables")
          .abbr("dt")
          .action((_, c) => c.copy(dropTables = true))
          .text("Удалить таблицы"),
      )
    cmd("init")
      .action((_, c) => c.copy(command = Commands.Init))
      .text("Создать таблицы")
      .children(
        opt[Unit]("force")
          .abbr("f")
          .action((_, c) => c.copy(force = true))
          .text("Пересоздать таблицы, если существуют")
      )
    cmd("extract")
      .action((_, c) => c.copy(command = Commands.Extract))
      .text("Выгрузить данные в csv формате")
      .children(
        opt[String]("query")
          .abbr("q")
          .required()
          .action((q, c) => c.copy(query = q))
          .valueName("<query>")
          .text("Запрос на выбор данных"),
        opt[String]("file")
          .required()
          .action((f, c) => c.copy(file = f))
          .valueName("<file>")
          .text("Файл, куда выгрузятся данные")
      )
    checkConfig { c =>
      if(c.command == Commands.NoCommand)
        failure("Нужно указать хотя бы одну команду")
      else
        success
    }
  }

  val DB = 'so

  def process(config: Config): Unit = {
    logger.info("Processing...")
    DBs.setup(DB)

    config match {
      case Config(Commands.Load, path, _, append, _, _, _) => load(path, append)
      case Config(Commands.Clean, _, _, _, dropTables, _, _) => clean(dropTables)
      case Config(Commands.Init, _, _, _, _, force, _) => init(force)
      case Config(Commands.Extract, _, file, _, _, _, query) => extract(file, query)
      case _ =>
    }

    DBs.closeAll()
    logger.info("Done")
  }

  def load(path: String, append: Boolean): Unit = {
    logger.info(f"""Loading (path = "$path", append = $append)""")
    if (!append) {
      dropTables()
      createTables()
    }

    val dataloader = new DataLoader {
      override def basePath: String = path
    }

    val data = dataloader.loadData()

    NamedDB(DB).autoCommit { implicit session =>
      lazy val u = User.column
      lazy val p = Post.column
      lazy val c = Comment.column

      data.foreach {
        case user: User =>
          withSQL {
            insert.into(User).namedValues(
              u.id -> user.id,
              u.displayName -> user.displayName,
              u.location -> user.location,
              u.about -> user.about,
              u.reputation -> user.reputation,
              u.views -> user.views,
              u.upVotes -> user.upVotes,
              u.downVotes -> user.downVotes,
              u.accountId -> user.accountId,
              u.creationDate -> user.creationDate,
              u.lastAccessDate -> user.lastAccessDate
            )
          }.update().apply()
        case post: Post =>
          withSQL {
            insert.into(Post).namedValues(
              p.id -> post.id,
              p.title -> post.title,
              p.body -> post.body,
              p.score -> post.score,
              p.viewCount -> post.viewCount,
              p.answerCount -> post.answerCount,
              p.commentCount -> post.commentCount,
              p.ownerUserId -> post.ownerUserId,
              p.lastEditorUserId -> post.lastEditorUserId,
              p.acceptedAnswerId -> post.acceptedAnswerId,
              p.creationDate -> post.creationDate,
              p.lastEditDate -> post.lastEditDate,
              p.lastActivityDate -> post.lastActivityDate,
              p.tags -> post.tags.mkString(",")
            )
          }.update().apply()
        case comment: Comment =>
          withSQL(
            insert.into(Comment).namedValues(
              c.id -> comment.id,
              c.postId-> comment.postId,
              c.score -> comment.score,
              c.text -> comment.text,
              c.creationDate -> comment.creationDate,
              c.userId -> comment.userId
            )
          ).update().apply()
        case _ =>
      }
    }
    logger.info("Loading completed")
  }

  def clean(dropTablesFlag: Boolean): Unit = {
    logger.info(f"Cleaning (drop_tables = $dropTablesFlag)")

    if (dropTablesFlag)
      dropTables()
    else
      truncateTables()
    logger.info(f"Cleaning completed")
  }

  def createTables(ifNotExists: Boolean=false): Unit = {
    logger.info("Creating tables")

    val condition = if (ifNotExists) sqls"if not exists" else sqls""

    NamedDB(DB).autoCommit {
      implicit session => {
        sql"""
            create table ${condition} USERS (
                id Int,
                display_name varchar(200),
                location varchar(200),
                about varchar(10000),
                reputation Int,
                views Int,
                up_votes Int,
                down_votes Int,
                account_id Int,
                creation_date Timestamp,
                last_access_date Timestamp
            );
          """.execute.apply()

        sql"""
            create table ${condition} POSTS (
                id Int,
                title varchar(200),
                body varchar(100000),
                score Int,
                view_count Int,
                answer_count Int,
                comment_count Int,
                owner_user_id Int,
                last_editor_user_id Int,
                accepted_answer_id Int,
                creation_date timestamp,
                last_edit_date timestamp,
                last_activity_date timestamp,
                tags varchar(3000)
            );
         """.execute.apply()

        sql"""
            create table ${condition} COMMENTS (
                id Int,
                post_id Int,
                score Int,
                text varchar(1000),
                creation_date timestamp,
                user_id Int
            );
         """.execute.apply()
      }
    }

    logger.info("Creating tables completed!")
  }

  val tableNames = Seq("Comments", "Posts", "Users")

  def dropTables(): Unit = {
    logger.info("Dropping tables")

    NamedDB(DB).autoCommit {
      implicit session => {
        tableNames.foreach { tableName =>
          val queryString = f"drop table if exists ${tableName};";
          sql"${SQLSyntax.createUnsafely(queryString)}".update().apply();
        }
      }
    }

    logger.info("Dropping completed!")
  }

  def truncateTables(): Unit = {
    logger.info("Truncating tables")

    NamedDB(DB).autoCommit {
      implicit session => {
        tableNames.foreach { table =>
          sql"truncate table ${ SQLSyntax.createUnsafely(table) };".execute.apply()
        }
      }
    }

    logger.info("Truncating completed!")
  }

  def init(force: Boolean): Unit = {
    logger.info(f"Initialization (force = $force)")
    if (force) {
      dropTables()
      createTables()
    }
    else
      createTables(ifNotExists = true)

    logger.info(f"Initialization completed")
  }

  def extract(file: String, queryString: String): Unit = {
    logger.info(s"""Extraction (file = "$file", query = "$queryString")""")

    var headers: IndexedSeq[String] = IndexedSeq()
    var result: List[IndexedSeq[Any]] = List()

    NamedDB(DB).readOnly{ implicit session =>
        result = sql"${SQLSyntax.createUnsafely(queryString)}".map{ rs =>
          val columnCount = rs.metaData.getColumnCount
          if (rs.index == 1)
            headers = for (i <- 1 to rs.metaData.getColumnCount) yield rs.metaData.getColumnName(i)
          for (i <- 1 to columnCount) yield rs.any(i)
        }.list().apply()
    }

    val writer = CSVWriter.open(new File(file))
    writer.writeAll(headers +: result)
    writer.close()

    logger.info(s"Extraction completed")
  }

  trait ORMObject[A] extends SQLSyntaxSupport[A] {
    override def connectionPoolName: Any = 'so
  }

  object User extends ORMObject[User] {

    override val tableName: String = "USERS"

    def apply(u: ResultName[User])(rs: WrappedResultSet): User = {
      new User(
        rs.int(u.id),
        rs.string(u.displayName),
        rs.string(u.location),
        rs.string(u.about),
        rs.int(u.reputation),
        rs.int(u.views),
        rs.int(u.upVotes),
        rs.int(u.downVotes),
        rs.int(u.accountId),
        rs.localDateTime(u.creationDate),
        rs.localDateTime(u.lastAccessDate)
      )
    }
  }

  object Post extends ORMObject[Post] {

    override val tableName: String = "POSTS"

    def apply(p: ResultName[Post])(rs: WrappedResultSet): Post = {
      new Post(
        rs.int(p.id),
        rs.string(p.title),
        rs.string(p.body),
        rs.int(p.score),
        rs.int(p.viewCount),
        rs.int(p.answerCount),
        rs.int(p.commentCount),
        rs.int(p.ownerUserId),
        rs.int(p.lastEditorUserId),
        rs.int(p.acceptedAnswerId),
        rs.localDateTime(p.creationDate),
        rs.localDateTime(p.lastEditDate),
        rs.localDateTime(p.lastActivityDate),
        rs.string(p.tags).split(",")
      )
    }
  }

  object Comment extends ORMObject[Comment] {

    override val tableName: String = "COMMENTS"

    def apply(c: ResultName[Comment])(rs: WrappedResultSet): Comment = {
      new Comment(
        rs.int(c.id),
        rs.int(c.postId),
        rs.int(c.score),
        rs.string(c.text),
        rs.localDateTime(c.creationDate),
        rs.int(c.userId)
      )
    }
  }

  logger.info(f"""Running "${args.mkString(" ")}"""")
  parser.parse(args, Config()) match {
    case Some(config) =>
      process(config)
    case _ =>
  }
}
