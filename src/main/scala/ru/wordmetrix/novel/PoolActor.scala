package ru.wordmetrix.novel
import scala.annotation.implicitNotFound
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import akka.pattern.pipe
import akka.pattern.ask
import spray.routing.RequestContext
import spray.json._
import DefaultJsonProtocol._
import scala.annotation.implicitNotFound
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import akka.pattern.pipe
import akka.pattern.ask
import spray.routing.RequestContext
import spray.json._
import DefaultJsonProtocol._
import spray.http._
import spray.http.StatusCodes

object PoolActor {
    abstract sealed trait PoolActorMessage
    import NovelService._
    
    case class RegisterMessage(id : Int,  actor : ActorRef)
    
    def props(articleprop: Props): Props = Props(new PoolActor(articleprop))
}

class PoolActor(articleprop: Props) extends Actor {
    import NovelService._
    import PoolActor._
    
    val ids = Iterator.from(1)
    
    def notFound(request: RequestContext ) {
        request.failWith(new IllegalRequestException(StatusCodes.NotFound))
    }
    
    override
    def preStart() = {
        context.become(receive(Map()))
    }

    def receive() : Receive = { case x => }
    
    def receive(map : Map[Int, ActorRef]) : Receive = {
        case msg @ GetMessage(id, Kind.Article | Kind.Phrase | Kind.Item, request) => 
            map.get(id) match {
                case Some(actor) => actor ! msg
                case None =>
                    notFound(request)         
            }
            
        case msg @ PostMessage(id, Kind.Article | Kind.Phrase | Kind.Item, request, _) =>
            map.get(id) match {
                case Some(actor) => actor ! msg
                case None =>
                    notFound(request)         
            }
            
        case msg @ PutMessage(id, Kind.Article | Kind.Phrase , request, state) =>
            map.get(id) match {
                case Some(actor) => 
                    println(s"put phrase in art $id")
                    actor ! msg.copy(payload = state match {
                        case state : Article => state.copy(id = ids.next())
                        case state : Phrase => state.copy(id = ids.next())
                        case state : Alternative => state.copy(id = ids.next())
                    })
                case None =>
                    notFound(request)         
            }
            
        case msg @ PutMessage(_, Kind.Shelve, request, state : Article) =>
            val id = ids.next
            val actor = context.actorOf(articleprop, s"Article-$id")
            actor ! PostMessage(id, Kind.Article, request, state.copy(id = id))
            context.become(receive(map + (id -> actor)))
            
        case RegisterMessage(id, actor) =>  
            println(s"register $id")
            context.become(receive(map + (id -> actor)))
            println(map)
    }
}