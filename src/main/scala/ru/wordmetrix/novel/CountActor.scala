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

object CountActor {
    abstract sealed trait CountActorMessage

    case class CountRequestMessage(request: RequestContext) extends CountActorMessage
    case class CountRequestJsonMessage(request: RequestContext) extends CountActorMessage
    case class CountMessage() extends CountActorMessage
    case class CountReplyMessage(count: Int) extends CountActorMessage
    case class CountState(n: Int)

    object NovelJsonProtocol extends DefaultJsonProtocol {
       implicit def countState = jsonFormat1(CountState)
    }
    
    def props(): Props = Props(new CountActor())
}

/**
 * Actor that serves short-time requests that requires an answer
 */
class CountActor() extends Actor {

    import CountActor._
    import CountActor.NovelJsonProtocol._

    protected implicit def executor: ExecutionContext =
        scala.concurrent.ExecutionContext.global

    implicit val defaultTimeout = Timeout(100 second)

    /**
     * Return sequence of words each has a length equal to according number
     */
    def receive(): Receive = {
        case CountMessage() =>
            println("count1")
            sender ! CountReplyMessage(1)
            context.become(count(CountState(1)))
    }

    def count(state: CountState): Receive = {
        case CountMessage() =>
            println("count2")
            val newstate = state.copy(n = state.n + 1)
            sender ! CountReplyMessage(newstate.n)
            context.become(count(newstate))

        case CountRequestMessage(request) =>
            request.complete {
                <html>
                    <body>
                        <h1>Novel's  wrtiting</h1>
                        <a href="/about.html">about</a>
                        <p> it was ${ state.n } request of that page </p>
                    </body>
                </html>
            }
        context.become(count(state.copy(n = state.n + 1)))

        case CountRequestJsonMessage(request) =>
            request.complete {
                state.toJson.toString
            }
        context.become(count(state.copy(n = state.n + 1)))

    }

    //            Future {
    //                EnWizMnemonic(
    //                    ns2ws(ns.map(x => if (x == 0) 10 else x), List(), List("", ""), System.currentTimeMillis() + 25000) {
    //                        case (n, word3) => !Set("'", ",", "-")(word3) && word3.length == n
    //                    } match {
    //                        case Left(x)  => Left(x.reverse)
    //                        case Right(x) => Right(x.reverse)
    //                    }
    //                )
    //            } pipeTo sender
}
