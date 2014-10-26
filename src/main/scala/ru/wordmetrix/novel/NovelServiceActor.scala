package ru.wordmetrix.novel

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import scala.annotation.implicitNotFound

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.util.Timeout
import akka.pattern.pipe
import akka.pattern.ask

object NovelServiceActor {
    abstract sealed trait EnWizMessage
    def props(counterprop: Props): Props =
        Props(new NovelServiceActor(counterprop))
}

class NovelServiceActor(counterprop: Props) extends Actor with NovelService {

  val counter = context.actorOf(counterprop, "Parser") 
    
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


