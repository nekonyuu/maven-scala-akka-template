package eu.nyuu.templates

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, concat, get, path, _}
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import eu.nyuu.templates.models.Response
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContextExecutor

object Main extends PlayJsonSupport {
  implicit val system: ActorSystem = ActorSystem.create("this-system")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val config: Config = ConfigFactory.load()

  def main(args: Array[String]) {
    Http().newServerAt("0.0.0.0", 8080).bindFlow(routes())
    logger.info(s"App started on 8080")
  }

  def routes(): Route = {
    concat(
      path("some" / "route" / Segment) {
        (id: String) =>
          get { context: RequestContext =>
            context.complete(
              Response(id = id, message = s"Hi, here's your id: $id")
            )
          }
      },
      path("some" / "other" / "route") {
        get {
          complete {
            Response(id = "foo", message = "Another silly message")
          }
        }
      }
    )
  }
}
