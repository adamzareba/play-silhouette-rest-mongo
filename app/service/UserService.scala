package service

import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait UserService extends IdentityService[User] {

  def save(user: User): Future[WriteResult]
}
