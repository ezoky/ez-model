package com.ezoky.ezmodel.actor

import akka.actor.Props
import akka.testkit.{ImplicitSender, TestKit}
import com.ezoky.ezmodel.actor.DomainClerk.CreateEntity
import com.ezoky.ezmodel.actor.DomainClerk.EntityAdded
import com.ezoky.ezmodel.actor.TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.Entities.{Entity, single}
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class DomainClerkTest
  extends TestKit(testSystem)
  with ImplicitSender
  with WordSpecLike {

  "Domain Actor" should {
    "reply 'EntityCreated' when asked to create an Entity" in {

      try {

        val domainId = "TestDomain"
        val entityId = "TestEntity"
        val test = testSystem.actorOf(Props(new DomainClerk(Name(domainId))), domainId)

//        test ! CreateEntity(Name(domainId),Name(entityId))
//        expectMsg(EntityAdded(Domain(Name(domainId),List(),List(Entity(Name(entityId))))))

      }
      finally {
        system.shutdown()
      }

    }
  }

}
