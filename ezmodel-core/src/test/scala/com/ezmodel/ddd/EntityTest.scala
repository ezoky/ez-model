package com.ezmodel.ddd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import Entity._

import scalaz._
import Scalaz._

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class EntityTest extends FunSuite {

  test("Equality of identity of DDD Entities is reflexive") {

    val e1 = Entity[Int,String](1)("id A")
    val e2 = Entity[Int,String](2)("id A")
    val e3 = Entity[Int,String](3)("id B")

    assert(e1.hasSameIdentity(e2) && e2.hasSameIdentity(e1))
    assert(!e1.hasSameIdentity(e3) && !e3.hasSameIdentity(e1))
  }

  test("Equality of states of DDD Entities is reflexive") {

    val e1 = Entity(1)("id A")
    val e2 = Entity(1)("id B")
    val e3 = Entity(2)("id C")

    assert(e1.hasSameState(e2) && e2.hasSameState(e1))
    assert(!e1.hasSameState(e3) && !e3.hasSameState(e1))
  }

  test("DDD entities are equal if they have same identity") {

    val e1 = Entity("A")(stateIdentify)
    val e2 = Entity("A")(stateIdentify)
    val e3 = Entity("B")(stateIdentify)

    assert(e1.hasSameIdentity(e2) && e1 == e2)
    assert(!e1.hasSameIdentity(e3) && (e1 !== e3))

    val e4 = Entity[String,String]("state 1")("id A")
    val e5 = Entity[String,String]("state 2")("id A")
    val e6 = Entity[String,String]("state 1")("id B")

    assert(e4 == e5)
    assert(e4 !== e6)
  }

  test("DDD entities without Identifier use their state as identifier by default") {

    val e1:Entity[String,String] = Entity[String,String]("A")_
    val e2:Entity[String,String] = Entity("A")(s => s)

    assert(e1.hasSameIdentity(e2) && e1 == e2)
  }

  test("Changing state of a DDD entity does not change its identity") {
    val e1 = Entity[String,String]("state 1")("id A")
    val e2 = e1.changeState("state 2")

    assert(!e1.hasSameState(e2))
    assert(e1.hasSameIdentity(e2))
    assert(e1 == e2)
  }

  test("DDD Entities are identical if and only if they have same identity and same state") {

    val e1 = Entity[String,String]("state 1")("id A")
    val e2 = Entity[String,String]("state 2")("id A")
    val e3 = Entity[String,String]("state 1")("id B")

    assert(!e1.isIdentical(e2))
    assert(!e1.isIdentical(e3))
    assert(e1.isIdentical(e2 + "state 1"))
  }

  test("Changing state to IdentityState does not change state") {

    val e1 = Entity[String,String]("state 1")("id A")

    assert(e1.hasSameState(e1 + IdentityState))
  }

  test("Cannot change state of an Entity to an InitialState") {

    val e1 = Entity[String,String]("A")("id 1")

    assert(e1.changeState(CommonInitialState("B")).state == CannotChangeToInitialState("B").left)
  }

  test("Cannot change state after FinalState is reached") {

    val e1 = Entity[String,String]("A")("id 1")
    val e2 = e1.changeState(CommonFinalState("B"))

    assert(e2.isStateFinal == true.right)
    assert(e2.changeState("C").state == CannotChangeFromFinalState("B").left)
  }


  test("Cannot change state after InvalidState is reached") {

    val e1 = Entity[String,String]("A")("id 1")
    val e2 = e1.changeState(CommonFinalState("B"))

    assert(e2.isStateFinal == true.right)

    val e3 = e2.changeState("C") // this is an invalid state
    assert(e3.isStateInvalid.isLeft)
    assert(e3.changeState("D").state == CannotChangeFromFinalState("B").left)
  }

  test("+ operator changes state") {

    val e1 = Entity[String,String]("A")("id")
    val state1 = "state 1"
    val state2 = "state 2"
    val e2 = e1 + state1 + state2
    val e3 = e1.changeState(state1).changeState(state2)

    assert(e2.hasSameState(e3))
  }

  test("Identity is immutable") {

    case class StateExample(id: Int, value: String)

    val stateId: Identify[StateExample, Int] = _.id

    val e1 = Entity(StateExample(1, "state 1"))(stateId)

    assert(e1.changeState(StateExample(2, "state 1")) == InvalidEntity(IdentityHasMutated,StateExample(2, "state 1"))(stateId).left)
  }

  test("Entities can have same state but be different if they have different ids") {

    case class StateExample(id1: Int, id2: Int)

    val state = StateExample(1, 2)

    val stateId1: Identify[StateExample, Int] = _.id1
    val stateId2: Identify[StateExample, Int] = _.id2

    val e1 = Entity(state)(stateId1)
    val e2 = Entity(state)(stateId2)

    assert(e1.hasSameState(e2) && (e1 !== e2))
  }

  test("Entities with different state structures can be seen as equal") {

    case class StateExample1(id1: Int, aVal: String)
    case class StateExample2(valS: String, id2: Int)

    val state1 = StateExample1(1, "val 1")
    val state2 = StateExample2("val 2", 1)

    val stateId1: Identify[StateExample1, Int] = _.id1
    val stateId2: Identify[StateExample2, Int] = _.id2

    val e1 = Entity(state1)(stateId1)
    val e2 = Entity(state2)(stateId2)

    assert(!e1.hasSameState(e2) && (e1 == e2))
  }
}
