package com.ezoky.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import com.ezoky.ezmodel.actor.EntityClerk._
import TestConfig._
import com.ezoky.ezmodel.core.Models._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.language.postfixOps

/**
 * @author gweinbach
 */
class EntityClerkTest
  extends TestKit(testSystem)
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  "Entity Actor" should {
    "reply 'AttributeAdded' when you add an attribute" in {

      try {

        val entityId = "Test Entity"
        val test = entityClerk(entityId)

        //expectMsg(EntityCreated(Entity(entityId)))

        //within(1 second) {
        //expectMsg(EntityCreated(Entity(entityId)))
        //}

        test ! AddAttribute(Name(entityId), Name("an attribute"), single, mandatory = true)
        expectMsg(AttributeAdded(Entity(Name(entityId))))
      }
      finally {
        TestKit.shutdownActorSystem(system)
      }

    }
  }

}
