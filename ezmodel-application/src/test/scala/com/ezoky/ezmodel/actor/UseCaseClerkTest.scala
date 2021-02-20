package com.ezoky.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import TestConfig._
import com.ezoky.ezmodel.actor.UseCaseClerk._
import com.ezoky.ezmodel.core.Models._
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.language.postfixOps

/**
 * @author gweinbach
 */
class UseCaseClerkTest
  extends TestKit(testSystem)
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  "UseCase Actor" should {
    "replies 'AttributeAdded' when you add an attribute" in {

      try {

        val actor = Actor(Name("an Actor"))
        val goal = Goal(Action(Verb("a Goal")))
        val test = useCaseClerk(actor, goal)

        //within(1 second) {
        //expectMsg(UseCaseCreated(UseCase(actor,goal)))
        //}

        val preCondition = EntityState(Entity(Name("entity")), StateName(Qualifier("a pre-condition state")))
        val postCondition = EntityState(Entity(Name("entity")), StateName(Qualifier("a post-condition state")))

        test ! AddPreCondition(actor, goal, preCondition)
        expectMsg(ConstrainedUseCase(UseCase(actor, goal) withPreCondition  preCondition))

        test ! AddPostCondition(actor, goal, preCondition)
        expectMsg(ConstrainedUseCase(UseCase(actor, goal) withPostCondition postCondition)) // equality of Use Cases is based on equality of their id, i.e. actor + goal
      }
      finally {
        TestKit.shutdownActorSystem(system)
      }
    }
  }

}
