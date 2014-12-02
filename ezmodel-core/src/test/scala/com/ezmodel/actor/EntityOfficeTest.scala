package com.ezmodel.actor

import akka.actor.ActorRef
import com.ezmodel.actor.EntityClerk.{AddAttribute, AttributeAdded}
import com.ezmodel.actor.Office._
import com.ezmodel.actor.TestConfig._
import com.ezmodel.core.Atoms.Name
import com.ezmodel.core.Entities.single
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
    entityOffice = system.actorOf(props[EntityClerk],"Entities")
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

