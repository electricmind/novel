package ru.wordmetrix.novel

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
// this trait defines our service behavior independently from the service actor
trait NovelService extends HttpService {

  val myRoute = 
    path("") {
       get {
           respondWithMediaType(`text/html`) {
               complete {
                 <html>
                   <body>
                     <h1>Novel's  wrtiting</h1>
                     <a href="/about.html">about</a>
                   </body>
                  </html>
               }
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