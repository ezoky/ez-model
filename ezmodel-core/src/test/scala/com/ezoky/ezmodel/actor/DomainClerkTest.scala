package com.ezoky.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import com.ezoky.ezmodel.actor.DomainClerk._
import com.ezoky.ezmodel.actor.TestConfig._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.Entities.Entity
import com.ezoky.ezmodel.core.UseCases.{UseCase, Goal, Actor}
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
    "reply '<something>Added' when asked to add <something>" in {

      try {

        val domainId = "TestDomain"
        val test = domainClerk(domainId)

        val entityName = Name("TestEntity")
        test ! CreateEntity(Name(domainId), entityName)
        expectMsg(EntityAdded(Domain(Name(domainId), List(), List(Entity(entityName)))))

        val actor = Actor("an Actor")
        val goal = Goal("a Goal")
        test ! CreateUseCase(Name(domainId),actor,goal)
        expectMsg(UseCaseAdded(Domain(Name(domainId), List(UseCase(actor,goal)), List(Entity(entityName)))))


      }
      finally {
        system.shutdown()
      }

    }
  }

}
