package com.ezoky.ezmodel.actor

import com.typesafe.config.{Config, ConfigFactory}
import akka.actor.ActorSystem

object TestConfig {
  val config = ConfigFactory.parseString("""
    akka {
      loggers = ["akka.testkit.TestEventListener"]
      loglevel = "DEBUG"
      log-config-on-start = off
      actor {
        debug {
          autoreceive = on
          lifecycle = on
          receive = on
          unhandled = on
        }
      }
      persistence.journal.plugin = "akka.persistence.journal.inmem"
    }""".stripMargin)

  def testSystem: ActorSystem = testSystem(TestConfig.config)

  def testSystem(config: Config) = {
    ActorSystem("Tests", config)
  }

}
