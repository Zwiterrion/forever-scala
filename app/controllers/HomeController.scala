package controllers
import javax.inject._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoggingAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    block(request)
  }
}

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val loggingAction: LoggingAction) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def hello = loggingAction {
    val user: Map[String, String] = Map()
    val posts: List[Map[String, String]] = List(
      Map(
        "author" -> "Shekhar",
        "body" -> "Getting started with Play"
      ),
      Map(
        "author" -> "Etienne",
        "body" -> "Un cours sur scala"
      ),
      Map()
    )
    Ok(views.html.hello("Posts", user, posts))
  }
}
