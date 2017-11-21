package dao

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

import scala.concurrent.{ExecutionContext, Future}

/**
  * An implementation of the auth info DAO which stores the data in database.
  */
class PasswordInfoDAOImpl @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit ex: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {

  /**
    * The data store for the auth info.
    */
  def passwords = reactiveMongoApi.database.map(_.collection[JSONCollection]("password"))

  implicit lazy val format = Json.format[PasswordInfo]

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    passwords.flatMap(_.find(Json.obj("loginInfoId" -> loginInfo.providerKey)).one[PasswordInfo])

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo  The auth info to add.
    * @return The added auth info.
    */
  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val passwordBuilder = Json.toJson(authInfo).as[JsObject] ++ Json.obj("loginInfoId" -> Some(loginInfo.providerKey))
    passwords.flatMap(_.insert(passwordBuilder)).flatMap {
      _ => Future.successful(authInfo)
    }
  }

  /**
    * Updates the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be updated.
    * @param authInfo  The auth info to update.
    * @return The updated auth info.
    */
  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val passwordBuilder = Json.toJson(authInfo).as[JsObject] ++ Json.obj("loginInfoId" -> Some(loginInfo.providerKey))
    passwords.flatMap(_.update(Json.obj("loginInfoId" -> loginInfo.providerKey), passwordBuilder)).flatMap {
      _ => Future.successful(authInfo)
    }
  }

  /**
    * Saves the auth info for the given login info.
    *
    * This method either adds the auth info if it doesn't exists or it updates the auth info
    * if it already exists.
    *
    * @param loginInfo The login info for which the auth info should be saved.
    * @param authInfo  The auth info to save.
    * @return The saved auth info.
    */
  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  /**
    * Removes the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be removed.
    * @return A future to wait for the process to be completed.
    */
  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    passwords.flatMap(_.remove(Json.obj("loginInfoId" -> loginInfo.providerKey))).flatMap(_ => Future.successful(()))
  }
}
