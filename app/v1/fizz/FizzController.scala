package v1.fizz

import javax.inject._
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent._

case class FizzFormData(name: String, start: String, end: String)
case class FizzGuessFormData(guess:String)

@Singleton
class FizzController @Inject()(implicit ec: ExecutionContext, cc: FizzControllerComponents) extends FizzBaseController(cc) {

  private val startEndConstraint: Constraint[FizzFormData] = Constraint("constraints.startend")({
    form =>
      if (form.start.toInt < form.end.toInt) Valid else Invalid(Seq(ValidationError("Start must be smaller than end.")))
  })

  private val createForm: Form[FizzFormData] = {
    import play.api.data.Forms._

    Form(
    mapping(
    "name" -> nonEmptyText(minLength = 3,maxLength = 3),
    "start" -> default(text,"1"),
    "end" -> default(text,"100")
    )(FizzFormData.apply)(FizzFormData.unapply)
    .verifying(startEndConstraint)
    )
  }

  private val guessForm: Form[FizzGuessFormData] = {
    import play.api.data.Forms._

    Form(
    mapping(
    "guess" -> nonEmptyText
    )(FizzGuessFormData.apply)(FizzGuessFormData.unapply)
    )
  }

  def list: Action[AnyContent] = FizzAction.async { implicit request:FizzRequest[AnyContent] =>
    fizzResourceHandler.list.map { game =>
      Ok(Json.toJson(game))
    }
  }

  def create: Action[AnyContent] = FizzAction.async { implicit request:FizzRequest[AnyContent] =>
    processCreate()
  }

  def find(id: String): Action[AnyContent] = FizzAction.async { implicit request =>
    fizzResourceHandler.find(id).map { game =>
      Ok(Json.toJson(game))
    }
  }

  def guess(id:String): Action[AnyContent] = FizzAction.async { implicit request =>
    processGuess(id)
  }

  private def processCreate[A]()(implicit request: FizzRequest[A]):Future[Result] = {
    createForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      form => fizzResourceHandler.create(form).map {
        case Some(id) => Created(Json.toJson(id.toString))
        case _ => InternalServerError("Oops")
      })
  }

  private def processGuess[A](id:String)(implicit request: FizzRequest[A]):Future[Result] = {
    guessForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      form => fizzResourceHandler.play(FizzGuessResource(id,form.guess)).map {
        case Some(game) => Ok(Json.toJson(game))
        case _ => InternalServerError("Oops")
      })
  }



}