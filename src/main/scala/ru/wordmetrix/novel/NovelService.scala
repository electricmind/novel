package ru.wordmetrix.novel

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import akka.pattern.pipe
import CountActor._
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import akka.pattern.pipe
import akka.pattern.ask
import scala.concurrent.duration._
import scala.util._
import scala.concurrent.ExecutionContext

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
// this trait defines our service behavior independently from the service actor
trait NovelService extends HttpService {
    
    val counter: akka.actor.ActorRef
    
    implicit val defaultTimeout = Timeout(100 second)
    implicit def executor: ExecutionContext =
        scala.concurrent.ExecutionContext.global
    val myRoute =
        path("") {
            get {
                respondWithMediaType(`text/html`) {
                    request =>
                        (counter ? CountMessage()) onComplete {
                            case Success(CountReplyMessage(n)) =>
                                println("test2")
                                request.complete {
                                    <html>
                                        <body>
                                          <h1>Novel's  wrtiting</h1>
                                            <a href="/about.html">about</a>
                                            <p> it was ${ n } request of that page </p>
                                        </body>
                                    </html>
                                }

                        }

                }
            }
        } ~
        path("count") {
            get {
                respondWithMediaType(`text/html`) {
                    request =>
                        counter ! CountRequestMessage(request)
                                        }
            }
        } ~
            path("about.html") {
                get {
                    respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
                        complete {
                            <html>
                                <body>
                                    <h1>Simple service to cooperative novels' writing</h1>
                                </body>
                            </html>
                        }
                    }
                }
            }

}