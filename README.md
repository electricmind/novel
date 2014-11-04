novel
=====

Simple server for cooperative writing


/*
* https://github.com/spray/twirl
* https://github.com/sbt/sbt-assembly
* http://slick.typesafe.com/download/

*/

/v1/article/<id>  {
  id : <id>,
  version : <version>
  phrases : [ <pid>, .... ,<pid> ]
}


/v1/phrase/<pid> {
  id : <pid>,
  version : <version>,
  flag : empty | current | chosen,
  choosen : <aid> | None
  alternatives : [ <aid>, ...., <aid> ]

}

/v1/alternative/<aid> {
  id : <aid>,
  version : <version>,
  phrase : "<text>"
  mark : <num>
}

/v1/alternative/<aid>/<user> {
  id : <mid>,
  mark : <num>
}


protocol:

/v1/article/put (create article and becomes leader) -> article id, leader id, user catalog

/v1/article/<id>/put <user tag> (user name -> user tag)

/v1/article/<id>/put <first phrase, keywords and so on> -> phrase

each user:

/v1/phrase/<id>/put <alternative> -> id phrase - first attempt creates, following attempts return early created

/v1/alternative/<id> post - can change the phrase until state is not "fixed"

when all of the alernatives entered:

/v1/phrase/<id>/put/state (set state for the phrase, might be done by service itself by time or if everybody completed) (everybody completed + 5 sec)

/v1/phrase/<id>/get <alternative list> 

each user for each alternative

/v1/alternative/<id>/put mark (set mark, first attempt creates, followed ones just update

/v1/mark/<id>/post (can change mark until state of phrase is not marked)

/v1/phrase/<id>/post <choosen phrase or just "marked"> (can be issued automatically)

----------------------------
another protocol:


/v1/put <create new article and return <id>

/v1/<id article>/user/put <create new usertag> (get from cookies and set into cookies, also can change the name)

/v1/<id article>/user/<tag>/post (changes the name, only if tag == cookie)

(user, id, cookie, name)

/v1/<id article>/put <create new phrase with keywords> state = 1

/v1/<id article/<phrase id>/put <create new alternative> (alternative is binded to usertag)

(when all of the alternatives creates phrase turns into state = 2

/v1/<id article>/<phrase id>/<altenative id>/put <mark> (mark is binded to user tag)

??? /v1/<id article>/<phrase id>/put <mark, alternative> -> result

When all of the marks entered phrase turns into state = 3 and next phrase are created

/v1/<id artic

----------------------------

  article : change, create, uservalidate 

  phrase : change, create

  alternative : change, create

  mark : 


  
-------------------------

 shelve :  CreateArticle

 article :

 sentence :

 usersentence :

 mark :

-------------------------

 /v1/put <subject> - shelve ! CreateArticle

 /v1/<aid>/user/put <usertag> -> usertag, Article ! RegisterUser, User ! CreateUser

 /v1/<aid>/put <Keywords> -> sid, Article ! CreateSentence #Automatic by time 

 /v1/<aid>/<sid>/put <userphrase> -> usid, Sentence ! CreatePhrase

 /v1/<aid>/<sid> -> list of usids

 /v1/<aid>/<sid>/<usid> -> UserSentence

 /v1/<aid>/<sid>/<usid>/put -> mid, UserSentence ! Mark, mark can not be edited, but can recreated, Mark can not be read.

     Sentence ask Article for usertags Map[Usertag, Option[Mark]]

     Put get usertag from mark

     If all of the usertags are busy it talk Sentence

     If all of the Usersentence reported they choosen, it elect one and change status (automatcially) and sents CreateSentence (might be thru keyword)




















