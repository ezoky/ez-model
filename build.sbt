

Global / onChangedBuildSource := ReloadOnSourceChanges

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

