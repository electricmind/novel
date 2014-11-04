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

object ArticleActor {
    abstract sealed trait ArticleActorMessage
    def props(phraseprop: Props): Props = Props(new ArticleActor(phraseprop))
}

class ArticleActor(phraseprop: Props) extends Actor {
    import NovelService._
    import NovelJsonProtocol._
    import PoolActor._
      
    override
    def preStart() = {
        context.become(receive(Article(0, "", 0, List(), List())))
    }
    
    def receive() : Receive = { case x => }
   
    def receive(article : Article) : Receive = {
        case msg @ GetMessage(id, Kind.Article, request) => 
            println("art actor get $id")
            request.complete {
                article.toJson.toString
            }            
            
        case msg @ PostMessage(id, Kind.Article, request, state : Article) =>
            println(s"post $state")
            request.complete {
                state.toJson.toString
            }        
            context.become(receive(state))
            
        case msg @ PutMessage(id, Kind.Article, request, phrase : Phrase) =>
             println(s"article actor put $id $phrase")
             val state = article.copy(phrases = phrase.id :: article.phrases)
             
             val phraseref = context.actorOf(phraseprop, s"Phrase-${phrase.id}")
             
             sender ! RegisterMessage(phrase.id, phraseref)
             
             phraseref ! PostMessage(phrase.id, Kind.Phrase, request, phrase)
             
             context.become(receive(state))
      }
}