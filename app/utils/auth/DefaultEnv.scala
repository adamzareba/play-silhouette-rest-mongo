package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import models.security.User

trait DefaultEnv extends Env {

  type I = User
  type A = JWTAuthenticator
}
