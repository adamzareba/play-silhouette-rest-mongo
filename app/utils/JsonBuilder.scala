package utils

import play.api.libs.json.JsObject

class JsonBuilder(var json: JsObject) {

  def ++[T](value: Option[T], addFn: (T) => JsObject) = {
    val newJson = value match {
      case Some(value) => Some(addFn(value))
      case _ => None
    }

    val ifPresent = newJson match {
      case Some(newJson) => Some(json.as[JsObject] ++ newJson)
      case _ => None
    }

    json = ifPresent.getOrElse(json)
    this
  }

  def get = json
}
