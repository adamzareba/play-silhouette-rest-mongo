package models.security

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.Json

case class User(id: Option[String], username: String, loginInfo: LoginInfo,
                firstName: String, lastName: String, activated: Boolean) extends Identity {}

object User {
  implicit val reader = Json.reads[User]
  implicit val writer = Json.writes[User]

  implicit val loginInfoReader = Json.reads[LoginInfo]
  implicit val loginInfowriter = Json.writes[LoginInfo]

  import play.api.libs.json._

  implicit object UserWrites extends OWrites[User] {
    def writes(user: User): JsObject =
      user.id match {
        case Some(id) =>
          Json.obj(
            "_id" -> user.id,
            "loginInfo" -> Json.obj(
              "providerID" -> user.loginInfo.providerID,
              "providerKey" -> user.loginInfo.providerKey
            ),
            "username" -> user.username,
            "firstName" -> user.firstName,
            "lastName" -> user.lastName,
            "activated" -> user.activated
          )
        case _ =>
          Json.obj(
            "loginInfo" -> Json.obj(
              "providerID" -> user.loginInfo.providerID,
              "providerKey" -> user.loginInfo.providerKey
            ),
            "username" -> user.username,
            "firstName" -> user.firstName,
            "lastName" -> user.lastName,
            "activated" -> user.activated
          )
      }

    implicit object UserReads extends Reads[User] {
      def reads(json: JsValue): JsResult[User] = json match {
        case user: JsObject =>
          try {
            val id = (user \ "_id" \ "$oid").asOpt[String]

            val providerId = (user \ "loginInfo" \ "providerId").as[String]
            val providerKey = (user \ "loginInfo" \ "providerKey").as[String]

            val username = (user \ "userName").as[String]
            val firstName = (user \ "firstName").as[String]
            val lastName = (user \ "lastName").as[String]
            val activated = (user \ "activated").as[Boolean]
            JsSuccess(
              new User(
                id,
                username,
                new LoginInfo(providerId, providerKey),
                firstName,
                lastName,
                activated
              )
            )
          } catch {
            case cause: Throwable => JsError(cause.getMessage)
          }
        case _ => JsError("expected.jsobject")
      }
    }

  }

}
