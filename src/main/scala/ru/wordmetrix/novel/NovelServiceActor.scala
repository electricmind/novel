package ru.wordmetrix.novel

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

class NovelServiceActor extends Actor with NovelService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


