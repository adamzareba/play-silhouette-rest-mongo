package dao

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import utils.JsonBuilder

import scala.concurrent.{ExecutionContext, Future}

class PasswordInfoDAOImpl @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit ex: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {

  def passwords = reactiveMongoApi.database.map(_.collection[JSONCollection]("password"))

  implicit lazy val format = Json.format[PasswordInfo]

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    passwords.flatMap(_.find(Json.obj("loginInfoId" -> loginInfo.providerKey)).one[PasswordInfo])

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val builder = new JsonBuilder(Json.toJson(authInfo).as[JsObject])
    builder ++[String](Some(loginInfo.providerKey), (value: String) =>
      Json.obj("loginInfoId" -> value))
    passwords.flatMap(_.insert(builder.get)).flatMap { result =>
      Future.successful(authInfo)
    }
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???
}
