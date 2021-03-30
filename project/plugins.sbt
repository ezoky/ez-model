
// SBT 1.4.0 : full dependency tree features as in old sbt-dependency-graph plugin
addDependencyTreePlugin

// Git inside sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.5")

// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.6.4")

// Scalafix plugin
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.26")

// Coverage plugin
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

// Scala 3 plugin
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.4.4")

// SBT assembly plugin to enable CLI and Console assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

// sbt-sonatype plugin used to publish artifact to maven central via sonatype nexus
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.5")
// sbt-pgp plugin used to sign the artifact with pgp keys
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")
