package com.ezmodel.actor

import akka.testkit.{ImplicitSender, TestKit}
import com.ezmodel.actor.TestConfig._
import com.ezmodel.actor.UseCaseClerk._
import com.ezmodel.core.Entities.{Entity, EntityState}
import com.ezmodel.core.UseCases._
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class UseCaseClerkTest
  extends TestKit(testSystem)
  with ImplicitSender
  with WordSpecLike {

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
        system.shutdown()
      }
    }
  }

}
