import Dependencies.scalaReflectModule
import com.typesafe.sbt.SbtGit
import sbt.Keys._
import sbt._

object Common {

  val warnOnUnusedImportsOption = settingKey[String]("'Warn on unused imports' scala compiler option")

  def computeWarnOnUnusedImportsVersion(scalaVersionValue: String) =
    CrossVersion.partialVersion(scalaVersionValue) match {
      case Some((2, n)) if n <= 11 =>
        "-Ywarn-unused-import"
      case Some((2, 12)) =>
        "-Ywarn-unused:imports"
      case _ =>
        "-Wunused:imports"
    }

  val defaultSettings = Seq(
    SbtGit.showCurrentGitBranch,
    conflictManager := ConflictManager.strict,
    libraryDependencies ++= Dependencies.Test.Minimal,
    dependencyOverrides ++= Dependencies.Overrides,

    organization := "com.ezoky",
    organizationName := "EZOKY",
    organizationHomepage := Some(url("http://ezoky.com/")),
    licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),

    publishTo := {
      if (isSnapshot.value)
        Some(Opts.resolver.sonatypeSnapshots)
      else
        Some(Opts.resolver.sonatypeStaging)
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),

    // sonatype credentials
    //    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.env.getOrElse("SONATYPE_USERNAME", "<undefined sonatype username>"),
      sys.env.getOrElse("SONATYPE_PASSWORD", "<undefined sonatype password>")
    ),

//    scalaReflectModule := Dependencies.`scala-reflect`(scalaVersion.value),

    warnOnUnusedImportsOption := computeWarnOnUnusedImportsVersion(scalaVersion.value),
    scalacOptions += warnOnUnusedImportsOption.value
  )
}
