package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import formatters.json.{CredentialFormat, Token}
import models.security.{SignUp, User}
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import service.UserService
import utils.auth.DefaultEnv
import utils.responses.rest.Bad

import scala.concurrent.Future

class SignUpController @Inject()(userService: UserService,
                                          configuration: Configuration,
                                          silhouette: Silhouette[DefaultEnv],
                                          clock: Clock,
                                          credentialsProvider: CredentialsProvider,
                                          authInfoRepository: AuthInfoRepository,
                                          passwordHasherRegistry: PasswordHasherRegistry,
                                          val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val credentialFormat = CredentialFormat.restFormat

  implicit val signUpFormat = Json.format[SignUp]

  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp].map { signUp =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
      userService.retrieve(loginInfo).flatMap {
        case None => /* user not already exists */
          val user = User(None, loginInfo.providerKey, loginInfo, signUp.firstName, signUp.lastName, true)
          // val plainPassword = UUID.randomUUID().toString.replaceAll("-", "")
          val authInfo = passwordHasherRegistry.current.hash(signUp.password)
          for {
            userToSave <- userService.save(user)
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(loginInfo)
            token <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(token,
              Ok(Json.toJson(Token(token = token, uuid = user.loginInfo.providerKey, expiresOn = authenticator.expirationDateTime)))
            )
          } yield {
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            //            Ok(Json.toJson(RegistrationResult(user, signUp.authInfo)))
            result
          }
        case Some(_) => /* user already exists! */
          Future(Conflict(Json.toJson(Bad(message = "user already exists"))))
      }
    }.recoverTotal {
      case error =>
        Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toFlatJson(error)))))
    }
  }

  //  def signOut = silhouette.SecuredAction.async { implicit request =>
  //    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
  //    request.authenticator.discard(Future.successful(Ok))
  //  }
}
