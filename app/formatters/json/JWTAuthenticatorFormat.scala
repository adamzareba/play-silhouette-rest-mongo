package formatters.json

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import org.joda.time.DateTime
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._

import scala.concurrent.duration.{FiniteDuration, _}

object JWTAuthenticatorFormat {

  implicit val jodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val jodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit object FiniteDurationFormat extends Format[FiniteDuration] {
    def reads(json: JsValue): JsResult[FiniteDuration] = LongReads.reads(json).map(_.seconds)

    def writes(o: FiniteDuration): JsValue = LongWrites.writes(o.toSeconds)
  }

  implicit object JWTAuthenticatorWrites extends OWrites[JWTAuthenticator] {
    def writes(authenticator: JWTAuthenticator): JsObject =
      Json.obj(
        "_id" -> authenticator.id,
        "loginInfo" -> authenticator.loginInfo,
        "lastUsedDateTime" -> authenticator.lastUsedDateTime,
        "expirationDateTime" -> authenticator.expirationDateTime,
        "idleTimeout" -> authenticator.idleTimeout
      )
  }

  implicit object JWTAuthenticatorReads extends Reads[JWTAuthenticator] {
    def reads(json: JsValue): JsResult[JWTAuthenticator] = json match {
      case authenticator: JsObject =>
        try {
          val id = (authenticator \ "_id").as[String]
          val providerId = (authenticator \ "authenticator" \ "loginInfo" \ "providerID").as[String]
          val providerKey = (authenticator \ "authenticator" \ "loginInfo" \ "providerKey").as[String]
          val lastUsedDateTime = (authenticator \ "authenticator" \ "lastUsedDateTime").as[DateTime]
          val expirationDateTime = (authenticator \ "authenticator" \ "expirationDateTime").as[DateTime]
          val idleTimeout = (authenticator \ "authenticator" \ "idleTimeout").asOpt[FiniteDuration]

          JsSuccess(
            new JWTAuthenticator(
              id,
              new LoginInfo(providerId, providerKey),
              lastUsedDateTime,
              expirationDateTime,
              idleTimeout
            )
          )
        } catch {
          case cause: Throwable => JsError(cause.getMessage)
        }
      case _ => JsError("expected.jsobject")
    }
  }

}
