package v1.fizz

import javax.inject._
import akka.actor.ActorSystem
import anorm._
import play.api.db.Database
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.{ExecutionContext, Future}

final case class FizzData(id: FizzId, name: String, current: String, start: String, end: String,status:String)

class FizzId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object FizzId {
  def apply(raw: String): FizzId = {
    require(raw != null)
    new FizzId(Integer.parseInt(raw))
  }
}

class FizzExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait FizzRepository {

  def list(): Future[Seq[FizzData]]
  def create(data: FizzData): Future[Option[FizzId]]
  def find(id:String):Future[Option[FizzData]]
  def update(data: FizzData):Future[Option[FizzData]]

}

@Singleton
class FizzRepositoryImpl @Inject()(implicit ec: FizzExecutionContext,db: Database) extends FizzRepository {


  val fizzGames = Seq(
    FizzData(FizzId("1"), "APT", "1","1","100","Created"),
    FizzData(FizzId("2"), "TTT", "3","1","100","Created"),
    FizzData(FizzId("3"), "REQ", "15","1","100","Created"),
    FizzData(FizzId("4"), "LCD", "5","1","100","Created"),
    FizzData(FizzId("5"), "LOL", "1","1","100","Created")
  )

  val listParser: RowParser[FizzData] = {
      SqlParser.int("id") ~
      SqlParser.str("name") ~
      SqlParser.str("current") ~
      SqlParser.str("first") ~
      SqlParser.str("last") ~
      SqlParser.str("status") map {
      case id ~ name ~ current ~ first ~ last ~ status =>
        FizzData(FizzId(id.toString),name.toUpperCase,current,first,last,status.toUpperCase)
    }
  }




  override def list(): Future[Seq[FizzData]] = {
    Future {
      db.withConnection { implicit c =>
        SQL("select * from games;").as(listParser *)
      }
    }
  }

  override def create(data: FizzData): Future[Option[FizzId]] = {
    // CREATE TABLE games (
    // id SERIAL PRIMARY KEY,name VARCHAR(3) NOT NULL,
    // current VARCHAR(100) NOT NULL,
    // first VARCHAR(100) NOT NULL,
    // last VARCHAR(100) NOT NULL,
    // status VARCHAR(100) NOT NULL
    // );

    // INSERT INTO games(name,current,first,last,status)
    // values ('TER', '1', '1', '100', 'CREATED')
    // RETURNING id;
    Future{
      db.withConnection { implicit c =>
        val result: Option[String] = SQL("insert into games(name,current,first,last,status) values ({name}, {current}, {start}, {end}, {status}) returning id")
          .on('name -> data.name, 'current -> data.current,'start -> data.start,'end -> data.end,'status -> data.status)
          .executeInsert().map(id => id.toString)

        result match {
          case Some(data) => Some(FizzId(data))
          case None => None
        }
      }
    }
  }

  override def find(id:String): Future[Option[FizzData]] = {
    Future {
      db.withConnection{ implicit c =>
        SQL(s"select * from games where id = ${id};").as(listParser.singleOpt)
      }
    }
  }

  override def update(data:FizzData):Future[Option[FizzData]] = {
    println(data)
    Future {
      db.withConnection{ implicit c =>
        SQL(s"UPDATE games SET current = {current}, status = {status} where id = {id} returning id,name,current,first,last,status;")
          .on("current" -> data.current,"status" -> data.status, "id"-> data.id.toString.toInt).as(listParser.singleOpt)
      }
    }
  }



}
