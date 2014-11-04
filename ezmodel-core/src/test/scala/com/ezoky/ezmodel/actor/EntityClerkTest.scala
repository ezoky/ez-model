package com.ezoky.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import com.ezoky.ezmodel.actor.EntityClerk._
import com.ezoky.ezmodel.actor.TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities.{Entity, single}
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class EntityClerkTest
  extends TestKit(testSystem)
  with ImplicitSender
  with WordSpecLike {

  "Entity Actor" should {
    "replies 'AttributeAdded' when you add an attribute" in {

      try {

        val entityId = "Test Entity"
        val test = entityClerk(entityId)

        //expectMsg(EntityCreated(Entity(entityId)))

        //within(1 second) {
          //expectMsg(EntityCreated(Entity(entityId)))
        //}

        test ! AddAttribute(Name(entityId),Name("an attribute"),single,mandatory = true)
        expectMsg(AttributeAdded(Entity(Name(entityId))))
      }
      finally {
        system.shutdown()
      }

    }
  }

}
