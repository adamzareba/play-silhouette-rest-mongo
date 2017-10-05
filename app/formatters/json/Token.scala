package formatters.json

import org.joda.time.DateTime
import play.api.libs.json._
import utils.JsonBuilder

/**
  * This class represent token
  *
  * @param token Id of token
  * @param expiresOn The expiration time
  */
case class Token(token: String, userId: String, expiresOn: DateTime)

object Token {

  implicit object TokenWrites extends OWrites[Token] {
    def writes(token: Token): JsObject = {
      val json = Json.obj(
        "token" -> token.token,
        "userId" -> token.userId,
        "expiresOn" -> token.expiresOn.toString
      )

      val builder = new JsonBuilder(json)
      builder.get
    }
  }
}
