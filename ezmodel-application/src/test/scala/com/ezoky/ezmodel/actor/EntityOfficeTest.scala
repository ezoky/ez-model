package com.ezoky.ezmodel.actor

import akka.actor.ActorRef
import com.ezoky.ezmodel.actor.EntityClerk.{AddAttribute, AttributeAdded}
import com.ezoky.ezmodel.actor.Office._
import TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities.single
/**
 * @author gweinbach
 */
class EntityOfficeTest extends ClerkTestKit[EntityClerk](testSystem) {

  val officeId = "Entities"

  override val domain = officeId

  "Entity clerk" should {
    "communicate outcome with persistent events" in {

      val entityName = Name("TestEntity")

      val entityOffice = system.actorOf(props[EntityClerk], "Entities")
      //      expectEventPersisted[EntityCreated,Name](entityName) {
      //      }

      expectEventPersisted[AttributeAdded, Name](entityName) {
        entityOffice ! AddAttribute(entityName, "attribute name", single, mandatory = true)
      }

      // kill reservation office and all its clerks (aggregate roots)
      ensureActorTerminated(entityOffice)
    }
  }
}

