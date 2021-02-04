
name := "ezmodel"

organization in ThisBuild := "com.ezoky"

lazy val distVersion = sys.props.getOrElse("distVersion", "0.1.0-SNAPSHOT")

version in ThisBuild := distVersion

scalaVersion in ThisBuild := Dependencies.Versions.scala

scalacOptions in ThisBuild ++= Seq(
  "-encoding", "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-Yrangepos", // use range positions for syntax trees
  "-language:postfixOps", //  enables postfix operators
  "-language:implicitConversions", // enables defining implicit methods and members
  "-language:existentials", // enables writing existential types
  "-language:reflectiveCalls", // enables reflection
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  /*"-Xlint",               // enable handy linter warnings
    "-Xfatal-warnings",     // turn compiler warnings into errors*/
)

autoCompilerPlugins := true

addCompilerPlugin(Dependencies.`kind-projector`)
addCompilerPlugin(Dependencies.`better-monadic-for`)


// Define the root project, and make it compile all child projects
lazy val `ezmodel` = project.in(file(".")).aggregate(
  `ezmodel-core`,
  `ezmodel-application`,
  `ezmodel-storage`
)

// This enables dependencies on junit.jar to run coverage tests from intelliJ
libraryDependencies += Dependencies.Test.junit

// Define individual projects, the directories they reside in, and other projects they depend on

lazy val `ezmodel-core` =
  project.in(file("ezmodel-core"))
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies ++= Dependencies.`cats-minimal`,
        libraryDependencies += Dependencies.`joda-time`,
        libraryDependencies += Dependencies.`ez-logging`,
      ): _*
    )

lazy val `ezmodel-storage` =
  project.in(file("ezmodel-storage"))
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies += Dependencies.`nscala-time`,
      ): _*
    )

lazy val `ezmodel-application` =
  project.in(file("ezmodel-application"))
    .dependsOn(`ezmodel-core`)
    .dependsOn(`ezmodel-storage`)
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies += Dependencies.`akka-actor`,
        libraryDependencies += Dependencies.`akka-persistence`,
        libraryDependencies += Dependencies.Test.`akka-testkit`
      ): _*
    )

Common.defaultSettings

