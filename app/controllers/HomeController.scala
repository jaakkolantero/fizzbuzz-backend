package controllers

import javax.inject._
import play.api.mvc._

class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action {
    Ok("howdy ho")
  }

}