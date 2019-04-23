package example

import endpoints.openapi.model.{Info, OpenApi}
import endpoints.{algebra, openapi}
import io.circe.generic.semiauto._
import io.circe._


case class Reference(key: String, value: Long)

object Reference {
  implicit def encoder: ObjectEncoder[Reference] = deriveEncoder[Reference]

  implicit def decoder: Decoder[Reference] = deriveDecoder[Reference]
}

case class SomeRequest(data: String, int: Int, flag: Option[Boolean], ref: Reference)

object SomeRequest {
  implicit def encoder: ObjectEncoder[SomeRequest] = deriveEncoder[SomeRequest]

  implicit def decoder: Decoder[SomeRequest] = deriveDecoder[SomeRequest]
}


case class SomeResponse(data: Int)

trait SomeEndpoint extends algebra.Endpoints with endpoints.algebra.JsonSchemaEntities {

  def someDocumentedResource(implicit req: JsonRequest[SomeRequest]): Endpoint[(Int, SomeRequest), String] = {
    val request = post(path / "some-resource" / segment[Int]("id"), jsonRequest[SomeRequest](Option("perform an action")))
    endpoint(
      request,
      textResponse(docs = Some("The content of the resource"))
    )
  }
}


object OpenApiEncoder extends endpoints.openapi.model.OpenApiSchemas with endpoints.circe.JsonSchemas {
  implicit def requestSchema: JsonSchema[SomeRequest] = JsonSchema(implicitly, implicitly)
}


object CirceDocs extends openapi.Endpoints with SomeEndpoint with openapi.JsonSchemaEntities with endpoints.generic.JsonSchemas with CirceAdapter {

  val api: OpenApi = {
    openApi(Info(title = "API to get some resource", version = "1.0")) {
      someDocumentedResource(document(SomeRequest("hello", 1, Option(true), Reference("key", 123))))
    }
  }

  import OpenApiEncoder.JsonSchema._
  import io.circe.Json
  import io.circe.syntax._

  val apiJson: Json = api.asJson

}

object Main extends App {
  println("open api docs:")
  println(CirceDocs.apiJson.spaces4)

}