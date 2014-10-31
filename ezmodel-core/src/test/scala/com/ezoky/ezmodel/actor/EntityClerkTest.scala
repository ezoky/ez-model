package com.ezoky.ezmodel.actor

import akka.actor.Props
import akka.testkit.{ImplicitSender, TestKit}
import com.ezoky.ezmodel.actor.EntityClerk.{AttributeAdded, AddAttribute, EntityCreated}
import com.ezoky.ezmodel.actor.TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities.{single, Entity}
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration._
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

        val entityId = "TestEntity"
        val test = testSystem.actorOf(Props(new EntityClerk(Name(entityId))), entityId)

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
