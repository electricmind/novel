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

object PhraseActor {
    abstract sealed trait PhraseActorMessage
    def props(alternativeprop: Props): Props = Props(new PhraseActor(alternativeprop))
}

class PhraseActor(alternativeprop: Props) extends Actor {
    import NovelService._
    import NovelJsonProtocol._
    import PoolActor._
      
    override
    def preStart() = {
        context.become(receive(Phrase(0, None, List(), List())))
    }
   
    def receive() : Receive = { case x => }
    
    def receive(phrase : Phrase) : Receive = {
        case msg @ GetMessage(id, Kind.Phrase, request) => 
            println(s"get phrase $id")
            request.complete {
                phrase.toJson.toString
            }
            
        case msg @ PostMessage(id, Kind.Phrase, request, state : Phrase) =>
            println(s"post phrase $id")
            request.complete {
                state.toJson.toString
            }        
            context.become(receive(state))
            
        case msg @ PutMessage(id, Kind.Phrase, request, alternative : Alternative) =>
            println(s"put phrase $id")
             val state = phrase.copy(alternatives = alternative.id :: phrase.alternatives)
             
             val alternativeref = context.actorOf(alternativeprop, s"alternative-${alternative.id}")
             
             sender ! RegisterMessage(alternative.id, alternativeref)
    
             alternativeref ! PostMessage(id, Kind.Item, request, alternative)
             
             context.become(receive(state))
    }
}