package formatters

import org.joda.time.DateTime
import play.api.libs.json._
import utils.JsonBuilder

/**
  * This class represent token
  *
  * @param token Id of token
  * @param expiresOn The expiration time
  */
case class Token(var token: String, var uuid: String, var expiresOn: DateTime)

object Token {

  implicit val reader = Json.reads[Token]
  implicit val writer = Json.writes[Token]

  implicit object TokenWrites extends OWrites[Token] {
    def writes(token: Token): JsObject = {
      var json = Json.obj(
        "token" -> token.token,
        "uuid" -> token.uuid,
        "expiresOn" -> token.expiresOn.toString
      )

      val builder = new JsonBuilder(json)
      builder.get
    }
  }
}
