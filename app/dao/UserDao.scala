package dao

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait UserDao {

  def save(user: User): Future[WriteResult]
  def find(loginInfo: LoginInfo): Future[Option[User]]
}
