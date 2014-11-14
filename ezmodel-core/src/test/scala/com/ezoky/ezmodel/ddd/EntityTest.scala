package com.ezoky.ezmodel.ddd

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import com.ezoky.ezmodel.ddd.State._

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

  test("Default state of a DDD entity is InitialState") {
    val e1 = Entity("A")

    assert(e1.state === InitialState)
  }

  test("Cannot change state after FinalState is reached") {
    val e1 = Entity("A")
    val e2 = e1.changeState(FinalState)

    intercept[CannotChangeFromFinalState] {
      e2.changeState("newState")
    }
  }
}
