package example

import endpoints.openapi.model.{Info, OpenApi}
import endpoints.{algebra, openapi}
import io.circe.generic.auto._


case class SomeRequest(data: String)

case class SomeResponse(data: Int)

trait SomeEndpoint extends algebra.Endpoints with endpoints.circe.JsonSchemas with endpoints.algebra.JsonSchemaEntities {

  implicit lazy val requestSchema: JsonSchema[SomeRequest] = JsonSchema(implicitly, implicitly)
  implicit lazy val responseSchema: JsonSchema[SomeResponse] = JsonSchema(implicitly, implicitly)

  val request = post(path / "some-resource" / segment[Int]("id"), jsonRequest[SomeRequest](Option("perform an action")))

  val someDocumentedResource =
    endpoint(
      request,
      textResponse(docs = Some("The content of the resource"))
    )
}

object OpenApiEncoder extends endpoints.openapi.model.OpenApiSchemas with endpoints.circe.JsonSchemas


//
//  this fails to compile
// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
object CirceDocs extends openapi.Endpoints with SomeEndpoint with openapi.JsonSchemaEntities {
  val api: OpenApi =
    openApi(Info(title = "API to get some resource", version = "1.0"))(
      someDocumentedResource
    )

  import OpenApiEncoder.JsonSchema._
  import io.circe.Json
  import io.circe.syntax._

  val apiJson: Json = api.asJson

}

object Main extends App {
  println("open api docs:")
  println(CirceDocs.apiJson.spaces4)
}