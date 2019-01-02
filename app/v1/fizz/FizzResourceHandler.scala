package v1.fizz

import FizzBuzz._
import javax.inject.{Inject, Provider}

import scala.concurrent.{Await, ExecutionContext, Future}
import play.api.libs.json._

import scala.concurrent.duration._
import scala.util.Success

case class FizzResource(id: String, name: String, current: String, start: String, end: String,status:String)

object FizzResource {

  /**
    * Mapping to write a FizzResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[FizzResource] {
    def writes(fizz: FizzResource): JsValue = {
      Json.obj(
        "id" -> fizz.id,
        "name" -> fizz.name,
        "current" -> fizz.current,
        "start" -> fizz.start,
        "end" -> fizz.end,
        "status" -> fizz.status
      )
    }
  }
}

case class FizzGuessResource(id:String,guess:String)

/**
  * Controls access to the backend data, returning [[FizzResource]]
  */
class FizzResourceHandler @Inject()(
                                     routerProvider: Provider[FizzRouter],
                                     fizzRepository: FizzRepository)(implicit ec: ExecutionContext) {

  val fizz = FizzBuzzRule("Fizz", number => Math.abs(number%3)==0)
  val buzz = FizzBuzzRule("Buzz", number => Math.abs(number%5)==0)
  val fizzBuzzGame = FizzBuzz("FizzBuzz Game", List(fizz,buzz))

  def list(): Future[Seq[FizzResource]] = {
    fizzRepository.list().map { dataList =>
      dataList.map(data => createFizzResource(data))
    }
  }

  def create(data: FizzFormData): Future[Option[FizzId]] = {
    fizzRepository.create(FizzData(FizzId("1"),data.name,data.start,data.start,data.end,"CREATED"))
  }

  def find(id: String):Future[Option[FizzResource]] = {
    fizzRepository.find(id).map {
      case Some(game) => Some(createFizzResource(game))
      case _ => None
    }
  }

  def play(data:FizzGuessResource):Future[Option[FizzResource]] = {

    fizzRepository.find(data.id).flatMap {
      case Some(value) => {
        val solvedPlay = solvePlay(value,data)
        update(solvedPlay)
      }
      case None => Future.successful(None)
    }


  }

  def solvePlay(game:FizzData, data:FizzGuessResource): FizzData = {
        val guess:Boolean = fizzBuzzGame.solve(List(game.current.toInt)).head.toLowerCase.trim == data.guess.toLowerCase.trim
        val status:Boolean = game.status != "FINISHED" && game.status != "COMPLETED"

        if (status && guess) {
          val newCurrent = (game.current.toInt+1).toString
          val newStatus = if(newCurrent.toInt >= game.end.toInt) "COMPLETED" else "STARTED"
          println(newCurrent)
          val newGameState = game.copy(current = newCurrent, status = newStatus)
          newGameState
        } else {
          game.copy(status = "FINISHED")
        }

  }


  def update(data:FizzData): Future[Option[FizzResource]] = {
    fizzRepository.update(data).map {
      case Some(game) => Some(createFizzResource(game))
      case _ => None
    }
  }

  private def createFizzResource(data: FizzData): FizzResource = {
    FizzResource(data.id.toString,data.name,data.current,data.start,data.end,data.status)
  }

}


