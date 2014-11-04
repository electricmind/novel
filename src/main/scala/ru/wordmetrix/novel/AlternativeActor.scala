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

object AlternativeActor {
    abstract sealed trait AlternativeActorMessage
    def props(): Props = Props(new AlternativeActor())
}

class AlternativeActor() extends Actor {
    import NovelService._
    import NovelJsonProtocol._
    import PoolActor._

    override def preStart() = {
        context.become(receive(Alternative(0, "", 0, List())))
    }

    def receive(): Receive = { case x => }

    def receive(phrase: Alternative): Receive = {
        case msg @ GetMessage(id, Kind.Item, request) =>
            request.complete {
                phrase.toJson.toString
            }

        case msg @ PostMessage(id, Kind.Item, request, state: Alternative) =>
            request.complete {
                state.toJson.toString
            }
            context.become(receive(state))
    }
}