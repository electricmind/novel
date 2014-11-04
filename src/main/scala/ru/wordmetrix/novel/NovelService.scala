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
import spray.json._
import DefaultJsonProtocol._
// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
// this trait defines our service behavior independently from the service actor

object NovelService {
    class NovelServiceMessage(request: RequestContext)
    object Kind extends Enumeration {
        type Kind = Value
        val Shelve, Article, Phrase, Item, Mark = Value
    }
    object State extends Enumeration {
        type State = Value
        val Edit, Choice = Value
    }

    import State._
    import Kind._
    case class GetMessage(id: Int, kind: Kind, request: RequestContext) extends NovelServiceMessage(request)
    case class PostMessage[U](id: Int, kind: Kind, request: RequestContext, payload: U) extends NovelServiceMessage(request)
    case class PutMessage[U](id: Int, kind: Kind, request: RequestContext, payload: U) extends NovelServiceMessage(request)

    
    case class Article(id: Int, title : String, state : Int, users: List[Int], phrases: List[Int])
    case class Phrase(id: Int, choosen : Option[String],keywords: List[String], alternatives: List[Int])
    case class Alternative(id: Int, phrase: String, mark : Int, marks : List[Int])
    case class Mark(id : Int, usertag : String, mark : Int)
    case class User(id : Int, usertag : String, name : String = "")

    object NovelJsonProtocol extends DefaultJsonProtocol {
        implicit def alternative = jsonFormat4(Alternative)
        implicit def phraseState = jsonFormat4(Phrase)
        //TODO : implicit def state = jsonFormat1(State)
        implicit def userState = jsonFormat3(User)
        implicit def markState = jsonFormat3(Mark)
        implicit def articleState = jsonFormat5(Article)
    }

}

trait NovelService extends HttpService {

    val counter: akka.actor.ActorRef
    val pool: akka.actor.ActorRef
    import NovelService._

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
        path("json") {
            get {
                respondWithMediaType(`application/json`) {
                    request =>
                        counter ! CountRequestJsonMessage(request)
                }
            }
        } ~
        path("article" / "put" / Segment) {
            title =>
                get {
                    request =>
                        println("create")
                        pool ! PutMessage(0, Kind.Shelve, request, Article(0, title, 0, List(), List()))
                    
                }  
        } ~
        path("article" / IntNumber) {
            id =>
                get {
                    request =>
                        println("art")
                        pool ! GetMessage(id, Kind.Article, request)
                } ~
                put {
                    request => pool ! PutMessage(id, Kind.Article, request, Phrase(0, None, List(), List()))
                }
        } ~
        path("article" / IntNumber / "put") {
            id =>
                get {
                        request =>
                            println("art put")
                            pool ! PutMessage(id, Kind.Article, request, Phrase(0, None, List(), List()))
                }
        } ~
        path("article" / IntNumber / Segment) {
            (id, title) => 
                (post | get) {  
                    parameters('status.as[Int] ? 0) { 
                        (status) => {
                            request => pool ! PostMessage(id, Kind.Article, request, Article(id, title, status, List(), List()))
                    } 
                } 
           }
        } ~
        path("phrase" / IntNumber) {
            id =>
                get {
                        request =>
                            println("phrase")
                            pool ! GetMessage(id, Kind.Phrase, request)
                } ~
                    post {
                        request => pool ! PostMessage(id, Kind.Phrase, request, Phrase(id, None, List(), List()))
                    } ~
                    put {
                        request => pool ! PutMessage(id, Kind.Phrase, request, Alternative(0, "", 0, List()))
                    }
        } ~
        path("phrase" / IntNumber / "put") {
            id =>
                get {
                    request => pool ! PutMessage(id, Kind.Phrase, request, Alternative(0, "", 0, List()))
                }
        } ~
        path("alternative" / IntNumber) {
            id =>
                get {
                        request =>
                            println("alternative")
                            pool ! GetMessage(id, Kind.Item, request)
                } ~
                    post {
                        request => pool ! PostMessage(id, Kind.Item, request, Alternative(id, "", 0, List()))
                    }
        } ~
        path("alternative" / IntNumber / "post" / Segment) {
            (id, s) =>
                get {
                    request => pool ! PostMessage(id, Kind.Item, request, Alternative(id, s, 0, List()))
                }
        } ~ 
        path("alternative" / IntNumber / "put") {
            id => List()
                get {
                    request => pool ! PutMessage(id, Kind.Mark, request, Mark(0, "", 0))
                }
        } ~
        path("about.html") {
            get {
                respondWithMediaType(`text/html`) { 
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