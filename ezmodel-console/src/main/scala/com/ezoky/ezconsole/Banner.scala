package com.ezoky.ezconsole

/**
  * @author gweinbach on 26/02/2021
  * @since 0.2.0
  */
trait Banner {

  val applicationName: String
  val asciiLogo: String
  val copyright: String

  lazy val scalaVersion = scala.util.Properties.versionNumberString
  lazy val javaVersion = System.getProperty("java.version")

  val applicationVersion = "$distversion" // This will be substituted by release script

  final lazy val actualApplicationVersion =
    if (applicationVersion == "$" + "distversion") { // cut it into 2 substring to avoid automated substitution by release script
      "dev"
    }
    else {
      applicationVersion
    }
}

trait ConsoleBanner extends Banner {

  val prompt: String

  final lazy val welcome: String =
    asciiLogo +
    s"""
       | ${copyright}
       |
       | ${applicationName} version ${actualApplicationVersion} (scala ${scalaVersion}, java ${javaVersion})
     """.stripMargin

}

trait EzokyAsciiBanner extends ConsoleBanner {

  override val copyright: String = "(C) EZOKY 2021"
  override val asciiLogo: String = """                   _
                                     |                  | |
                                     |  ___  ____  ___  | | __ _   _
                                     | / _ \|_  / / _ \ | |/ /| | | |
                                     ||  __/ / / | (_) ||   < | |_| |
                                     | \___|/___| \___/ |_|\_\ \__, |
                                     |                          __/ |
                                     |                         |___/ """.stripMargin
}
