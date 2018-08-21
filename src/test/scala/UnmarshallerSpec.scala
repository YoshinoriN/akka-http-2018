import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MarshallingDirectives._
import akka.http.scaladsl.server.directives.MethodDirectives._
import akka.http.scaladsl.server.directives.PathDirectives._
import akka.http.scaladsl.server.directives.RouteDirectives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, Unmarshaller }
import io.circe
import io.circe.generic.auto._
import io.circe.parser._
import org.scalatest.FunSpec

import scala.concurrent.Future

case class Train(id: Int, name: String)

class UnmarshallerSpec extends FunSpec with ScalatestRouteTest {
  it("basic") {
    implicit val trainUnmarshaller: FromEntityUnmarshaller[Train] =
      Unmarshaller.byteStringUnmarshaller
        .forContentTypes(ContentTypes.`application/json`)
        .flatMap { ex => mat => byteString =>
          val res: Either[circe.Error, Train] = parse(byteString.utf8String).flatMap(_.as[Train])
          res match {
            case Right(json) =>
              Future.successful(json)
            case Left(err) =>
              Future.failed(err)
          }
        }
    val route: Route = (path("train") & post & entity(as[Train])) { train =>
      complete(s"id: ${train.id}, name: ${train.name}")
    }

    val postReq = HttpRequest(
      method = HttpMethods.POST,
      uri = Uri("/train"),
      entity = HttpEntity("""{"id":100,"name":"fuga"}"""),
      headers = List(`Content-Type`(ContentTypes.`application/json`)))
    postReq ~> route ~> check {
      assert(entityAs[String] === """id: 100, name: fuga""")
    }
  }
}
