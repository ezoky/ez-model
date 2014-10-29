name := "ezmodel-core"

version := "1.0.0"

scalaVersion := "2.11.2"

val akkaVersion = "2.3.6"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.0"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion

