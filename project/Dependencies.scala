
import Dependencies.Versions.Google
import sbt._

object Dependencies {

  object Versions {

    val scala211 = "2.11.12"
    val scala212 = "2.12.12"
    val scala213 = "2.13.3"
    val scala3 = "3.0.0-M1"

    val scala = scala213
    val dotty = scala3

    val ScalaParserCombinator = "1.1.2"

    val Scalaz = "7.3.1"

    val Cats = "2.1.1"
    val Kittens = "2.1.0"
    val Akka = "2.6.8"
    val Refined = "0.9.13"
    val PlayJson = "2.7.4" // might be different from Play Version
    val Spark = "2.4.3"

    val JodaTime = "2.10.6"
    val NScalaTime = "2.24.0"
    val JodaConvert = "2.2.1"
    val JodaMoney = "1.0.1"

    val BetterFiles = "3.9.1"

    val EzLogging = "0.1.0"

    object Google {
      val GoogleSheets = "v4-rev568-1.25.0"
    }

    val Ammonite = "1.6.6"
    val Config = "1.3.4"
    val PdiJWT = "4.0.0"
    val Auth0 = "0.8.3"

    object Test {
      val SLF4J = "1.7.30"

      val Logback = "1.2.3"

      val JunitInterface = "0.11"
      val Concordion = "2.2.0"
      val Specs2 = "4.8.3"
      val Scalatest = "3.2.2"
      val ScalatestPlusScalacheck = "3.2.2.0"
      val ScalatestPlusPlay = "4.0.3"
      val Scalacheck = "1.14.3"
      val ScalacheckShapeless = "1.2.3"

      val MockitoScala = "1.16.0"
    }

  }

  // Scala libraries
  def `scala-reflect`(scalaVersionValue: String): ModuleID = "org.scala-lang" % "scala-reflect" % scalaVersionValue
  val `scala-parser-combinator` = "org.scala-lang.modules" %% "scala-parser-combinators" % Versions.ScalaParserCombinator

  // Compiler plugin
  val `better-monadic-for` = "com.olegpy" %% "better-monadic-for" % "0.3.1"
  val `kind-projector` = "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full

  // Joda
  val `joda-time` = "joda-time" % "joda-time" % Versions.JodaTime
  val `nscala-time` = "com.github.nscala-time" %% "nscala-time" % Versions.NScalaTime
  val `joda-money` = "org.joda" % "joda-money" % Versions.JodaMoney
  // contains annotations required by Scala to load joda-time classes but not necessary for java
  val `joda-convert` = "org.joda" % "joda-convert" % Versions.JodaConvert

  // ScalaZ
  val `scalaz-core` = "org.scalaz" %% "scalaz-core" % Versions.Scalaz
  val `scalaz-effect` = "org.scalaz" %% "scalaz-effect" % Versions.Scalaz
  val `scalaz-typelevel` = "org.scalaz" %% "scalaz-typelevel" % Versions.Scalaz
  val scalaz = Seq(`scalaz-core`)

  // Cats
  val `cats-macros` = "org.typelevel" %% "cats-macros" % Versions.Cats
  val `cats-kernel` = "org.typelevel" %% "cats-kernel" % Versions.Cats
  val `cats-core` = "org.typelevel" %% "cats-core" % Versions.Cats
  val `cats-mtl` = "org.typelevel" %% "cats-mtl" % Versions.Cats
  // Minimal dependencies to use cats library
  val `cats-minimal` = Seq(`cats-macros`, `cats-kernel`, `cats-core`)
  val kittens = "org.typelevel" %% "kittens" % Versions.Kittens
  // cf Test object for cats-laws

  // Akka
  val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % Versions.Akka
  val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % Versions.Akka
  val `akka-persistence` = "com.typesafe.akka" %% "akka-persistence" % Versions.Akka

  // Refined
  val refined = "eu.timepit" %% "refined" % Versions.Refined
  val `refined-cats` = "eu.timepit" %% "refined-cats" % Versions.Refined // optional
  val `refined-eval` = "eu.timepit" %% "refined-eval" % Versions.Refined // optional, JVM-only
  val `refined-jsonpath` = "eu.timepit" %% "refined-jsonpath" % Versions.Refined // optional, JVM-only
  val `refined-pureconfig` = "eu.timepit" %% "refined-pureconfig" % Versions.Refined // optional, JVM-only
  val `refined-scalacheck` = "eu.timepit" %% "refined-scalacheck" % Versions.Refined // optional
  val `refined-scalaz` = "eu.timepit" %% "refined-scalaz" % Versions.Refined // optional
  val `refined-scodec` = "eu.timepit" %% "refined-scodec" % Versions.Refined // optional
  val `refined-scopt` = "eu.timepit" %% "refined-scopt" % Versions.Refined // optional
  val `refined-shapeless` = "eu.timepit" %% "refined-shapeless" % Versions.Refined // optional

  // LevelDB
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val `leveldbjni-all` = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

  // Play
  val `play-json` = "com.typesafe.play" %% "play-json" % Versions.PlayJson

  // Misc
  val `slf4j-api` = "org.slf4j" % "slf4j-api" % Versions.Test.SLF4J
  // use only in test scope or in distributable applications
  val `logback-classic` = "ch.qos.logback" % "logback-classic" % Versions.Test.Logback
  val `typesafe-config` = "com.typesafe" % "config" % Versions.Config

  // Choco
  val `choco-solver` = "org.choco-solver" % "choco-solver" % "4.0.6"

  // JWT token handling
  val `pdi-jwt-core` = "com.pauldijou" %% "jwt-core" % Versions.PdiJWT
  val `pdi-jwt-play` = "com.pauldijou" %% "jwt-play" % Versions.PdiJWT
  val `pdi-jwt` = Seq(`pdi-jwt-core`, `pdi-jwt-play`)

  // Auth0
  val `auth0` = "com.auth0" % "jwks-rsa" % Versions.Auth0

  // Spark Sql
  val `spark-sql` = "org.apache.spark" %% "spark-sql" % Versions.Spark

  // A comprehensive and dev friendly Lens library
  val quicklens = "com.softwaremill.quicklens" %% "quicklens" % "1.4.12"

  // better files: a dependency-free pragmatic thin Scala wrapper around Java NIO - https://github.com/pathikrit/better-files
  val `better-files` = "com.github.pathikrit" %% "better-files" % Versions.BetterFiles

  // Google Sheet API
  val `google-api-services-sheets` = "com.google.apis" % "google-api-services-sheets" % Google
    .GoogleSheets exclude("commons-logging", "commons-logging")

  // Ammonite REPL, Scripts and Shell
  val `ammonite` = "com.lihaoyi" % "ammonite" % Versions.Ammonite cross CrossVersion.full

  // EZOKY
  val `ez-logging` = "com.ezoky" %% "ez-logging" % Versions.EzLogging

  object Test {
    // Full multi-version scala JuUnit support. Pulls JUnit
    val `junit-interface` = "com.novocode" % "junit-interface" % Versions.Test.JunitInterface % sbt.Test
    val scalatest = "org.scalatest" %% "scalatest" % Versions.Test.Scalatest % sbt.Test
    val concordion = "org.concordion" % "concordion" % Versions.Test.Concordion % sbt.Test

    val scalacheck = "org.scalacheck" %% "scalacheck" % Versions.Test.Scalacheck % sbt.Test
    val `scalacheck-shapeless` =
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % Versions.Test.ScalacheckShapeless % sbt.Test
    val `scalatest-plus-scalacheck` =
      "org.scalatestplus" %% "scalacheck-1-14" % Versions.Test.ScalatestPlusScalacheck% sbt.Test
    val `scalacheck-minimal` = Seq(
      scalacheck,
      `scalatest-plus-scalacheck`
    )

    val `specs2-core` = "org.specs2" %% "specs2-core" % Versions.Test.Specs2 % sbt.Test
    val `specs2-scalacheck` = "org.specs2" %% "specs2-scalacheck" % Versions.Test.Specs2 % sbt.Test
    val `specs2-mock` = "org.specs2" %% "specs2-mock" % Versions.Test.Specs2 % sbt.Test
    val `specs2-junit` = "org.specs2" %% "specs2-junit" % Versions.Test.Specs2 % sbt.Test
    val `specs2-matcher` = "org.specs2" %% "specs2-matcher" % Versions.Test.Specs2 % sbt.Test

    val `scalaz-scalacheck-binding` = "org.scalaz" %% "scalaz-scalacheck-binding" % Versions.Scalaz % sbt.Test
    val `scalacheck-toolbox-datetime` = "com.47deg" %% "scalacheck-toolbox-datetime" % "0.3.1" % sbt.Test

    val `cats-laws` = "org.typelevel" %% "cats-laws" % Versions.Cats % sbt.Test
    val `cats-laws-scalacheck` = Seq(
      `cats-laws`,
      `scalacheck-shapeless`
    )

    val `akka-testkit` = "com.typesafe.akka" %% "akka-testkit" % Versions.Akka % sbt.Test

    val `mockito-scala` = "org.mockito" %% "mockito-scala" % Versions.Test.MockitoScala % sbt.Test

    val Minimal = Seq(
      Dependencies.Test.scalatest,
      Dependencies.`logback-classic` % sbt.Test,
      // This dependency on junit.jar enables to run JUnit tests from sbt and coverage tests from intelliJ
      Dependencies.Test.`junit-interface`
    )
  }

  // Should be added to `dependencyOverrides`
  val Overrides = Seq(
    Dependencies.`slf4j-api` // to force SLF4J version over the one pulled by logback
  )

  val scalaReflectModule = settingKey[ModuleID]("scala-reflect module depending on current scala version")
}