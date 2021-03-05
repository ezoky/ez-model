import Dependencies.scalaReflectModule
import sbt.Keys.libraryDependencies

Global / onChangedBuildSource := ReloadOnSourceChanges

// used to sign jars
Global / pgpSigningKey := sys.env.get("PGP_SIGNING_KEY")
//Global / pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)
//Global / useGpgPinentry := true

name := "ez-model"
description := "A modelling and application building platform aimed to raise the level of abstraction of code"

homepage := Some(url("https://github.com/ezoky/ez-model"))
scmInfo := Some(ScmInfo(url("https://github.com/ezoky/ez-model"), "git@github.com:ezoky/ez-model.git"))
developers := List(Developer("gweinbach", "Grégory Weinbach", "gweinbach@ezoky.com", url("https://github.com/gweinbach")))


lazy val distVersion = sys.props.getOrElse("distVersion", "0.2.0-SNAPSHOT")

ThisBuild / version := distVersion

ThisBuild / scalaVersion := Dependencies.Versions.scala
lazy val supportedScalaVersions = List(
  Dependencies.Versions.scala213
)

ThisBuild / scalacOptions ++= Seq(
  "-Yrangepos", // use range positions for syntax trees
  "-language:postfixOps", //  enables postfix operators
  "-language:implicitConversions", // enables defining implicit methods and members
  "-language:existentials", // enables writing existential types
  "-language:reflectiveCalls", // enables reflection
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-encoding", "UTF-8", // source files are in UTF-8
  "-deprecation", // warns about use of deprecated APIs
  "-unchecked", // warns about unchecked type parameters
  "-feature", // warns about misused language features
  "-Xlint", // enables handy linter warnings
  //  "-Xfatal-warnings", // turns compiler warnings into errors
)

// Enables SemanticDB compiler for Scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
// Enables Scalafix complex rules to work with Scala 2.13
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)


autoCompilerPlugins := true

//addCompilerPlugin(CompilerPlugin.`kind-projector`)
addCompilerPlugin(CompilerPlugin.`better-monadic-for`)


// Define the root project, and make it compile all child projects
lazy val `ezmodel` =
  project.in(file("."))
    .aggregate(
      `ez-commons`,
      `ez-console`,
      `ezmodel-core`,
      `ezmodel-interaction`,
      `ezmodel-console`
    )
    .settings(skip in publish := true)
    .disablePlugins(sbtassembly.AssemblyPlugin)

// This enables dependencies on junit.jar to run coverage tests from intelliJ
libraryDependencies += Dependencies.Test.`junit-interface`

// Define individual projects, the directories they reside in, and other projects they depend on

lazy val `ez-commons` =
  project.in(file("ez-commons"))
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies += Dependencies.`ez-logging`
      ): _*
    )
    .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val `ez-console` =
  project.in(file("ez-console"))
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies += Dependencies.`scala-compiler`,
        libraryDependencies += Dependencies.`ez-logging`
      ): _*
    )
    .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val `ezmodel-core` =
  project.in(file("ezmodel-core"))
    .dependsOn(`ez-commons`)
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies ++= Dependencies.`cats-minimal`,
        libraryDependencies += Dependencies.`ez-logging`
      ): _*
    )
    .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val `ezmodel-interaction` =
  project.in(file("ezmodel-interaction"))
    .dependsOn(`ezmodel-core`)
    .settings(
      Common.defaultSettings ++ Seq(
        libraryDependencies += Dependencies.shapeless
      ): _*
    )
    .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val `ezmodel-console` =
  project.in(file("ezmodel-console"))
    .dependsOn(
      `ezmodel-interaction`,
      `ez-console`
    )
    .settings(
      Common.defaultSettings ++ Seq(
        mainClass in assembly := Some("com.ezoky.ezmodel.console.EzModellerConsole"),
        //        assembledMappings in assembly += {
        //          sbtassembly.MappingSet(None, Vector(
        //            ((baseDirectory.value / "conf" / "dev" / "logback.xml") -> "logback.xml"),
        //            ((baseDirectory.value / "conf" / "dev" / "application.conf") -> "application.conf")
        //          ))
        //        },
        //        assemblyMergeStrategy in assembly := {
        //          // Merge config files
        //          case PathList(ps@_*) if ps.last endsWith ".conf" => MergeStrategy.concat
        //          case o =>
        //            val oldStrategy = (assemblyMergeStrategy in assembly).value
        //            oldStrategy(o)
        //        },
        assemblyJarName in assembly := "ezmodel-console.jar"
      ): _*
    )

Common.defaultSettings

