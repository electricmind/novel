organization  := "ru.wordmetrix.novel"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test"
  )
}

Revolver.settings

//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

//libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test"

//libraryDependencies += "org.scalatest" %% "scalatest" % "2.0.M6" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

//libraryDependencies += "org.mongodb" %% "casbah" % "2.7.1"






