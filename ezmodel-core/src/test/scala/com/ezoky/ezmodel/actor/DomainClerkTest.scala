package com.ezoky.ezmodel.actor

import akka.testkit.TestKit
import com.ezoky.ezmodel.actor.DomainClerk._
import TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.Entities.Entity
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}

import scala.language.postfixOps

/**
 * @author gweinbach
 */
class DomainClerkTest
  extends ClerkTestKit[DomainClerk](testSystem) {

  override val domain = "Domains"

  "Domain Actor" should {
    "reply '<something>Added' when asked to add <something>" in {

      val domainId = "a Domain"
      var test = domainClerk(domainId)

      val entityName = Name("an Entity")
      test ! CreateEntity(Name(domainId), entityName)
      expectMsg(EntityAdded(Domain(Name(domainId), List(), List(Entity(entityName)))))

      val actor = Actor("an Actor")
      val goal = Goal("a Goal")
      test ! CreateUseCase(Name(domainId), actor, goal)
      expectMsg(UseCaseAdded(Domain(Name(domainId), List(UseCase(actor, goal)), List(Entity(entityName)))))

      ensureActorTerminated(test)
    }
  }

  "Domain Actor Commands" should {
    "be idempotent and add only once when asked to add an Entity twice" in {

      val domainId = "another Domain"
      var test = domainClerk(domainId)

      val entityName = Name("another Entity")
      test ! CreateEntity(Name(domainId), entityName)
      expectMsg(EntityAdded(Domain(Name(domainId), List(), List(Entity(entityName)))))

      test ! CreateEntity(Name(domainId), entityName)
      expectNoMessage()

      ensureActorTerminated(test)
    }
  }

  "Domain Actor Commands" should {
    "be idempotent and add only once when asked to add a Use Case twice" in {

      val domainId = "yet another Domain"
      var test = domainClerk(domainId)

      val actor = Actor("another Actor")
      val goal = Goal("another Goal")
      test ! CreateUseCase(Name(domainId), actor, goal)
      expectMsg(UseCaseAdded(Domain(Name(domainId), List(UseCase(actor, goal)), List())))

      test ! CreateUseCase(Name(domainId), actor, goal)
      expectNoMessage()

      ensureActorTerminated(test)
    }
  }

  override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }
}
