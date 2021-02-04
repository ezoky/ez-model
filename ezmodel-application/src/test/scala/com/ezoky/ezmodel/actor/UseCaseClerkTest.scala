package com.ezoky.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import TestConfig._
import com.ezoky.ezmodel.actor.UseCaseClerk._
import com.ezoky.ezmodel.core.Entities.{Entity, EntityState}
import com.ezoky.ezmodel.core.UseCases._
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

        val actor = Actor("an Actor")
        val goal = Goal("a Goal")
        val test = useCaseClerk(actor, goal)

        //within(1 second) {
        //expectMsg(UseCaseCreated(UseCase(actor,goal)))
        //}

        val preCondition = EntityState(Entity("an entity"), "a pre-condition state")
        val postCondition = EntityState(Entity("an entity"), "a post-condition state")

        test ! AddPreCondition(actor, goal, preCondition)
        expectMsg(ConstrainedUseCase(PreCondition(UseCase(actor, goal), preCondition)))

        test ! AddPostCondition(actor, goal, preCondition)
        expectMsg(ConstrainedUseCase(PostCondition(UseCase(actor, goal), postCondition))) // equality of Use Cases is based on equality of their id, i.e. actor + goal
      }
      finally {
        TestKit.shutdownActorSystem(system)
      }
    }
  }

}
