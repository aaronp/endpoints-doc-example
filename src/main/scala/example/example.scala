package example

import akka.http.scaladsl.server.Route
import endpoints.openapi.model.{Info, OpenApi}
import endpoints.{algebra, openapi}
import io.circe.generic.semiauto._
import io.circe.{Decoder, ObjectEncoder}


case class SomeRequest(data: String)
object SomeRequest {
  implicit def encoder: ObjectEncoder[SomeRequest] = deriveEncoder[SomeRequest]
  implicit def decoder: Decoder[SomeRequest] = deriveDecoder[SomeRequest]
}



case class SomeResponse(data: Int)

trait SomeEndpoint extends algebra.Endpoints with endpoints.algebra.JsonSchemaEntities { //with endpoints.algebra.JsonSchemas

  //  implicit def requestSchema: JsonSchema[SomeRequest] //= lazySchema(implicitly, implicitly)
  //  implicit def responseSchema: JsonSchema[SomeResponse] // = lazySchema(implicitly, implicitly)


  def someDocumentedResource(implicit req: JsonRequest[SomeRequest]): Endpoint[(Int, SomeRequest), String] = {
    val request = post(path / "some-resource" / segment[Int]("id"), jsonRequest[SomeRequest](Option("perform an action")))
    endpoint(
      request,
      textResponse(docs = Some("The content of the resource"))
    )
  }
}

object AkkaServer extends endpoints.circe.JsonSchemas with endpoints.akkahttp.server.JsonSchemaEntities with SomeEndpoint {
  implicit def requestSchema: JsonSchema[SomeRequest] = JsonSchema(implicitly, implicitly)

  //  implicit def someReq = jsonRequest[SomeRequest](Option("meh"))

  val route: Route = someDocumentedResource.implementedBy {
    case (a, b) => s"got $a w/ $b"
  }
}


object OpenApiEncoder extends endpoints.openapi.model.OpenApiSchemas with endpoints.circe.JsonSchemas


//
//  this fails to compile
// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
object CirceDocs extends openapi.Endpoints with SomeEndpoint with openapi.JsonSchemaEntities {

  import OpenApiEncoder._

  //  val foo = jsonRequest[SomeRequest](Option("perform an action"))


  val api: OpenApi = {
    //    val x: _root_.example.CirceDocs.DocumentedJsonSchema = implicitly[JsonRequest[SomeRequest]]

    implicit val x: OpenApiEncoder.JsonSchema[SomeRequest] = OpenApiEncoder.JsonSchema.apply[SomeRequest](implicitly, implicitly)
    implicit def requestSchema: OpenApiEncoder.JsonSchema[SomeRequest] = OpenApiEncoder.JsonSchema[SomeRequest](SomeRequest.encoder, SomeRequest.decoder) //(implicitly, implicitly)
    val someReq: Option[CirceDocs.DocumentedRequestEntity] = jsonRequest[SomeRequest](Option("foo"))(requestSchema)

    implicit val ffsreq: JsonRequest[SomeRequest] = ???
//    implicit val sc: DocumentedJsonSchema = null // ??? //lazySchema(x)
//    val r = jsonRequest[SomeRequest](None)
    //    val r : JsonRequest[SomeRequest] = jsonRequest[SomeRequest](None)
    openApi(Info(title = "API to get some resource", version = "1.0"))(
      someDocumentedResource
    )
  }

  import OpenApiEncoder.JsonSchema._
  import io.circe.Json
  import io.circe.syntax._

  val apiJson: Json = api.asJson

}

object Main extends App {
  println("open api docs:")
  println(CirceDocs.apiJson.spaces4)
  println("akka route :")
  println(AkkaServer.route)

}