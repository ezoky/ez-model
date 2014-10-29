package com.ezoky.ezmodel.actor

import akka.actor.{ActorLogging, Props, ActorRef}
import com.ezoky.ezmodel.actor.EntityClerk.{AddAttribute, AttributeAdded, CreateEntity, EntityCreated}
import com.ezoky.ezmodel.actor.TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities.{Entity, single}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class EntityOfficeTest extends ClerkTestKit[EntityClerk](testSystem) {

  var entityOffice: ActorRef = system.deadLetters
  val officeId = "Entities"

  override val domain = officeId

  before {
    entityOffice = system.actorOf(Props(Office[EntityClerk]), officeId)
  }

  after {
    ensureActorTerminated(entityOffice)
  }

  "Entity clerk" should {
    "communicate outcome with persistent events" in {

      val entityName = Name("TestEntity")

//      expectEventPersisted[EntityCreated,Name](entityName) {
//      }

      expectEventPersisted[AttributeAdded,Name](entityName) {
        entityOffice ! AddAttribute(entityName,"attribute name",single,mandatory = true)
      }

      // kill reservation office and all its clerks (aggregate roots)
      ensureActorTerminated(entityOffice)
    }
  }


}

