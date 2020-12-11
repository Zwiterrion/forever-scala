package controllers

import play.api.cache.AsyncCacheApi
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class KeyValue(key: String, value: String)

object KeyValue {
  implicit val reads: Reads[KeyValue] = Json.reads[KeyValue]
  implicit val writes: OWrites[KeyValue] = Json.writes[KeyValue]
}

@Singleton
class KeysController @Inject()(
                                ws: WSClient, cache: AsyncCacheApi,
                                val controllerComponents: ControllerComponents,
                                implicit val ec : ExecutionContext)
  extends BaseController {

//    val futureSearch: Future[WSResponse] =
//      ws.url("http://www.google.fr/q")
//        .withHttpHeaders("Accept"-> "application/json")
//        .withRequestTimeout(10000.seconds)
//        .withQueryStringParameters("search" -> "play")
//        .get()

    def get(key: String): Action[AnyContent] = Action.async {
        cache.get[String](key)
          .map {
            case Some(value) => Ok(Json.obj(
              "key" -> key,
              "value" -> value
            ))
            case _ => Ok(Json.obj())
        }
    }

    def post(): Action[JsValue] = Action.async(parse.json) { request =>
        request.body.validate[KeyValue]
          .map {
              keyValue =>
                  cache.set(keyValue.key, keyValue.value)
                    .map { _ =>
                        Created
                    }
          }
          .recoverTotal {
              _ => Future.successful(NotFound("Detected error"))
          }
    }

    def remove(key: String): Action[AnyContent] = Action.async {
        cache.get(key)
          .flatMap {
              case Some(_) =>
                  cache.remove(key)
                    .map { _ =>
                        NoContent
                    }
              case _ => Future.successful(BadRequest("key not found"))
          }
    }
}
