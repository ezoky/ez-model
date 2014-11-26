package com.ezoky.ezmodel.ddd

import com.ezoky.ezmodel.ddd.Identity._
import com.ezoky.ezmodel.ddd.State._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.language.implicitConversions

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class EntityTest extends FunSuite {

  test("Equality of identity of DDD Entities is reflexive") {

    val e1 = Entity("A")
    val e2 = Entity("A")
    val e3 = Entity("B")

    assert(e1.hasSameIdentity(e2) && e2.hasSameIdentity(e1))
    assert(!e1.hasSameIdentity(e3) && !e3.hasSameIdentity(e1))
  }

  test("Equality of states of DDD Entities is reflexive") {

    val e1 = Entity("A", 1)
    val e2 = Entity("B", 1)
    val e3 = Entity("C", 2)

    assert(e1.hasSameState(e2) && e2.hasSameState(e1))
    assert(!e1.hasSameState(e3) && !e3.hasSameState(e1))
  }

  test("DDD entities are equal if they have same identity") {

    val e1 = Entity("A")
    val e2 = Entity("A")
    val e3 = Entity("B")

    assert(e1.hasSameIdentity(e2) && e1 === e2)
    assert(!e1.hasSameIdentity(e3) && (e1 !== e3))

    val e4 = Entity("A", "state 1")
    val e5 = Entity("A", "state 2")
    val e6 = Entity("B", "state 1")

    assert(e4 === e5)
    assert(e4 !== e6)
  }

  test("Changing state of a DDD entity does not change its identity") {
    val e1 = Entity("A", "state 1")
    val e2 = e1.changeState("state 2")

    assert(!e1.hasSameState(e2))
    assert(e1.hasSameIdentity(e2))
    assert(e1 === e2)
  }

  test("DDD Entities are identical if and only if they have same identity and same state") {

    val e1 = Entity("A", "state 1")
    val e2 = Entity("A", "state 2")
    val e3 = Entity("B", "state 1")

    assert(!e1.isIdentical(e2))
    assert(!e1.isIdentical(e3))
    assert(e1.isIdentical(e2 + "state 1"))
  }

  test("Changing to Identity State does not change state") {

    val e1 = Entity("A", "state 1")

    assert(e1.hasSameState(e1 + IdentityState))
  }

  test("Default state of a DDD entity is InitialState") {
    val e1 = Entity("A")

    assert(e1.state === InitialState)
  }

  test("Cannot change state after FinalState is reached") {
    implicit val classTag = ""

    val e1 = Entity("A")
    val e2 = e1.changeState(FinalState)

    intercept[CannotChangeFromFinalState] {
      e2.changeState("newState")
    }
  }

  test("+ operator changes state") {

    val e1 = Entity("A")
    val state1 = "state 1"
    val state2 = "state 2"
    val e2 = e1 + state1 + state2
    val e3 = e1.changeState(state1).changeState(state2)

    assert(e2.hasSameState(e3))
  }
}
