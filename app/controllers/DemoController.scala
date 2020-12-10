package controllers

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

case class User(id: Int, name: String, firstName: String)

object User {
  implicit val userToJson = Json.format[User]
  implicit val userReads = Json.reads[User]
}

object Datastore {
  val users: mutable.HashMap[Int, User] = mutable.HashMap()

  def get(id: Int): Option[User] = users get id

  def create(id: Int, data: User): Either[ValidationError, User] = {
    users get id match {
      case Some(_) => Left(ValidationError("user already existed"))
      case _ =>
        users += id -> data
        Right(data)
    }
  }
  def update(id: Int, data: User): Either[ValidationError, User] = {
    users get id match {
      case Some(_) =>
          users += id -> data
          Right(data)
      case _ => Left(ValidationError("user not found"))
    }
  }
  def delete(id: Int): Unit = {
    users -= id
  }
}

@Singleton
class DemoController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(Json.obj(
      "users" -> Datastore.users
    ))
  }

  def create(): Action[AnyContent] = Action { request =>
    request.body.asJson match {
      case Some(value) =>
        val userFromJson: JsResult[User] = Json.fromJson[User] (value)
        userFromJson match {
          case e @ JsError(_) => Results.BadRequest(JsError.toJson(e).toString())
          case JsSuccess(u: User, _) =>
            Datastore.create (u.id, u) match {
              case Left(value) => Results.BadRequest(value.message)
              case Right(_) => Redirect("/users")
            }
        }
      case _ => Results.BadRequest("missing body")
    }
  }

  def get(id: Int): Action[AnyContent] = Action {
    Datastore.get(id) match {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound
    }
  }

  def put(id: Int): Action[AnyContent] = Action { request =>
    request.body.asJson match {
      case Some(jsValue) =>
        val userFromJson: JsResult[User] = Json.fromJson[User](jsValue)
        userFromJson match {
          case e @ JsError(_) => Results.BadRequest(JsError.toJson(e).toString())
          case JsSuccess(u: User, _) =>
            Datastore.update(id, u) match {
              case Left(value) => Results.BadRequest(value.message)
              case Right(_) => Ok("updated")
            }
        }
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    val usersSize = Datastore.users.size
    Datastore.delete(id)
    if (usersSize == Datastore.users.size)
      BadRequest("user id not found")
    else
      Ok("user removed")
  }

}
