package v1.fizz

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class FizzRouter @Inject()(controller: FizzController) extends SimpleRouter {
  val prefix = "/v1/fizzbuzz"

  override def routes: Routes = {
    case GET(p"/games") =>
      controller.list
    case POST(p"/games") =>
      controller.create
    case GET(p"/games/$id") =>
      controller.find(id)
    case PATCH(p"/games/$id") =>
      controller.guess(id)
  }

}