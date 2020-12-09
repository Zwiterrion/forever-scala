package controllers

import play.api.data.validation.ValidationError
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.HashMap

case class User(id: Int, name: String, firstName: String)

object Datastore {
  val users: HashMap[Int, User] = HashMap()

  def get(id: Int): Option[User] = users get id

  def create(id: Int, data: User): Either[ValidationError, User] = {
    users get id match {
      case Some(_) => Left(ValidationError("user already existed"))
      case _ =>
        users += (id, data)
        Right(data)
    }
  }
  def update(id: Int, data: User): Either[ValidationError, User] = {
    users get id match {
      case Some(_) =>
          users += (id, data)
          Right(data)
      case _ => Left(ValidationError("user not found"))
    }
  }
  def delete(id: Int, data: User): Unit = {
    users -= (id, data)
  }
}

@Singleton
class DemoController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  def greeting(msg: String) = Action {
    Ok(Json.obj(
      "message" -> msg
    ))
  }

  def index() = Action {
    
  }

  def create() = Action {

  }

  def get(id: Int) = Action {

  }

  def put(id: Int) = Action {

  }

  def delete(id: Int) = Action {

  }

}
