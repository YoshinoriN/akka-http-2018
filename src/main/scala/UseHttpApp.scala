import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.{ HttpApp, Route }

object UseHttpApp {
  def main(args: Array[String]): Unit = {
    WebServer.startServer("localhost", 8080)
  }

  object WebServer extends HttpApp {
    override def routes: Route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
  }
}
