package controllers

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import scala.concurrent.Future
import context.ExecutionContextImplicit._

case class User(id: Int, name: String, firstName: String)

object User {
  implicit val userToJson: OFormat[User] = Json.format[User]
  implicit val userReads: Reads[User] = Json.reads[User]
}

object Datastore {
  val users: mutable.HashMap[Int, User] = mutable.HashMap()

  def get(id: Int): Future[Option[User]] = Future {
    users get id
  }

  def create(id: Int, data: User): Future[Either[ValidationError, User]] = Future {
    users get id match {
      case Some(_) => Left(ValidationError("user already existed"))
      case _ =>
        users += id -> data
        Right(data)
    }
  }
  def update(id: Int, data: User): Future[Either[ValidationError, User]] = Future {
    users get id match {
      case Some(_) =>
          users += id -> data
          Right(data)
      case _ => Left(ValidationError("user not found"))
    }
  }
  def delete(id: Int): Future[Unit] = Future {
    users -= id
  }
}

@Singleton
class DemoController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action.async {
    Future(Ok(Json.obj(
      "users" -> Datastore.users
    )))
  }

  def create(): Action[AnyContent] = Action.async { request =>
    request.body.asJson match {
      case Some(value) =>
        val userFromJson: JsResult[User] = Json.fromJson[User] (value)
        userFromJson match {
          case e @ JsError(_) => Future.successful(BadRequest(JsError.toJson(e).toString()))
          case JsSuccess(u: User, _) =>
            Datastore.create (u.id, u).map {
              case Left(value) => BadRequest(value.message)
              case Right(_) => Redirect("/users")
            }
        }
      case _ => Future.successful(BadRequest("missing body"))
    }
  }

  def get(id: Int): Action[AnyContent] = Action.async {
    Datastore.get(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound
    }
  }

  def put(id: Int): Action[AnyContent] = Action.async { request =>
    request.body.asJson match {
      case Some(jsValue) =>
        val userFromJson: JsResult[User] = Json.fromJson[User](jsValue)
        userFromJson match {
          case e @ JsError(_) => Future.successful(BadRequest(JsError.toJson(e).toString()))
          case JsSuccess(u: User, _) =>
            Datastore.update(id, u).map {
              case Left(value) => BadRequest(value.message)
              case Right(_) => Ok("updated")
            }
        }
    }
  }

  def delete(id: Int): Action[AnyContent] = Action.async {
    val usersSize = Datastore.users.size
    Datastore.delete(id)
    Future {
      if (usersSize == Datastore.users.size)
        BadRequest("user id not found")
      else
        Ok("user removed")
    }
  }

}
