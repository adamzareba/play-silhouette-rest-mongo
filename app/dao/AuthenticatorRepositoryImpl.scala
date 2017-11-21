package dao

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import formatters.json.JWTAuthenticatorFormat._
import play.api.libs.json.Writes._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatorRepositoryImpl @Inject()(reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) extends AuthenticatorRepository[JWTAuthenticator] {

  def tokens = reactiveMongoApi.database.map(_.collection[JSONCollection]("jwt.auth.repo"))

  override def find(id: String): Future[Option[JWTAuthenticator]] = {
    tokens.flatMap(_.find(Json.obj("_id" -> id)).one[JWTAuthenticator])
  }

  override def add(authenticator: JWTAuthenticator): Future[JWTAuthenticator] = {
    val duration = 12 hours
    val obj = Json.obj("_id" -> authenticator.id, "authenticator" -> authenticator, "duration" -> duration)
    tokens.flatMap(_.insert(obj)).flatMap(_ => Future(authenticator))
  }

  override def update(authenticator: JWTAuthenticator) = ???

  override def remove(id: String): Future[Unit] =
    tokens.flatMap(_.remove(Json.obj("_id" -> id))).map(_ => id)
}