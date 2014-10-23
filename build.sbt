organization  := "ru.wordmetrix.novel"

version       := "0.1"

scalaVersion  := "2.10.2"

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
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.typesafe.slick"  %%  "slick"         % "2.1.0",
    "org.scalatest"       %%  "scalatest"     % "2.0.M6" % "test",
    "org.scalacheck"      %%  "scalacheck"    % "1.10.1" % "test",
    "junit"               %   "junit"         % "4.10" % "test",
    "org.mongodb"         %%  "casbah"        % "2.7.1",
    "com.netflix.rxjava"  % "rxjava-scala"    % "0.15.1",
    "org.ccil.cowan.tagsoup" % "tagsoup"      % "1.2.1"
    //"org.scala-lang" % "scala-compiler" % "2.10.0"
    //"org.scala-lang" % "scala-reflect" % "2.10.0"
  )
}

Revolver.settings

Twirl.settings

tomcat()

