package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import formatters.{CredentialsFormatter, Token}
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import service.UserService
import utils.auth.DefaultEnv

import scala.concurrent.Future

class AuthenticationController @Inject()(userService: UserService,
                                         configuration: Configuration,
                                         silhouette: Silhouette[DefaultEnv],
                                         clock: Clock,
                                         credentialsProvider: CredentialsProvider,
                                         authInfoRepository: AuthInfoRepository,
                                         passwordHasherRegistry: PasswordHasherRegistry,
                                         val messagesApi: MessagesApi)
  extends Controller
    with I18nSupport {

  implicit val credentialFormat =
    CredentialsFormatter.format

  def authenticate = Action.async(parse.json[Credentials]) { implicit request =>
    val credentials =
      Credentials(request.body.identifier, request.body.password)
    credentialsProvider
      .authenticate(credentials)
      .flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if !user.activated =>
            Future.failed(new IdentityNotFoundException("Couldn't find user"))
          case Some(user) =>
            val c = configuration.underlying
            silhouette.env.authenticatorService
              .create(loginInfo)
              .map {
                case authenticator => authenticator
              }
              .flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService
                  .init(authenticator)
                  .flatMap { token =>
                    silhouette.env.authenticatorService
                      .embed(
                        token,
                        Ok(
                          Json.toJson(
                            Token(
                              token,
                              uuid = user.loginInfo.providerID,
                              expiresOn = authenticator.expirationDateTime
                            )
                          )
                        )
                      )
                  }
              }
          case None =>
            Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }
      .recover {
        case e: ProviderException =>
          Forbidden
      }
  }
}
