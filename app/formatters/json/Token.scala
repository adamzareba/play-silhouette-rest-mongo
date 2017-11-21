package formatters.json

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * This class represent token
  *
  * @param token Id of token
  * @param expiresOn The expiration time
  */
case class Token(token: String, expiresOn: DateTime)

object Token {

  implicit object TokenWrites extends OWrites[Token] {
    def writes(token: Token): JsObject = {
      val json = Json.obj(
        "token" -> token.token,
        "expiresOn" -> token.expiresOn.toString
      )

      json
    }
  }
}
