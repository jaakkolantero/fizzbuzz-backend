package v1.fizz

import javax.inject.Inject
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait FizzRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider
class FizzRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest(request) with FizzRequestHeader

class FizzActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                                 (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[FizzRequest, AnyContent]
    with HttpVerbs {

  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type FizzRequestBlock[A] = FizzRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A],
                              block: FizzRequestBlock[A]): Future[Result] = {

    val future = block(new FizzRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}

case class FizzControllerComponents @Inject()(fizzActionBuilder: FizzActionBuilder,
                                              fizzResourceHandler: FizzResourceHandler,
                                              actionBuilder: DefaultActionBuilder,
                                              parsers: PlayBodyParsers,
                                              messagesApi: MessagesApi,
                                              langs: Langs,
                                              fileMimeTypes: FileMimeTypes,
                                              executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents

class FizzBaseController @Inject()(pcc: FizzControllerComponents) extends BaseController {
  override protected def controllerComponents: ControllerComponents = pcc

  def FizzAction: FizzActionBuilder = pcc.fizzActionBuilder

  def fizzResourceHandler: FizzResourceHandler = pcc.fizzResourceHandler
}