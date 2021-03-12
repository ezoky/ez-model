package com.ezoky.ezmodel.interaction.interpreter.eventsourcing

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec
import shapeless.HNil

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
class EventSourcingInterpretingSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "the Interpreter" should {
    "be able to update a state given a list of statements" in {

      // Modelling activity is restricted to Parsing
      object EventSourcing extends EventSourcing
      import EventSourcing._

      case class State(total: Int)

      Given("a language syntax")

      case object AddOne
      case object SubOne

      And("some sourced events")

      case object OneAdded
      case object OneSubbed

      And("some published events")
      case class AdditionPerformed(i: Int)

      And("some interpreters directives")

      implicit val addOneEventSourcer: EventSourcer[State, AddOne.type, OneAdded.type, AdditionPerformed] =
        EventSourcer.define(
          _ => _ => OneAdded,
          (s, _: OneAdded.type) => s.copy(total = s.total + 1),
          (_, _: OneAdded.type) => Some(AdditionPerformed(1))
        )

      implicit val subOneEventSourcer: EventSourcer[State, SubOne.type, OneSubbed.type, AdditionPerformed] =
        EventSourcer.define(
          _ => _ => OneSubbed,
          (s, _: OneSubbed.type) => s.copy(total = s.total - 1),
          (_, _: OneSubbed.type) => Some(AdditionPerformed(-1))
        )

      And("a defined initial state ")

      val initialState = State(10)


      When("a statement is given")

      val statement1 = Statement(AddOne)

      Then("the interpreter will produce events accordingly")

      val event1 = Interpreter(initialState, statement1)(addOneEventSourcer.interpreter)

      assert(event1 === OneAdded)

      And("the test event sourcing interpreter will update state")

      val (persistedEvent1, state1, publishedEvent1) = EventSourcer(initialState, statement1)

      assert(persistedEvent1 === OneAdded)
      assert(state1 === State(11))
      assert(publishedEvent1 === Some(AdditionPerformed(1)))


      When("a program (a list of Statements) is given")

      val statement2 = Statement(AddOne :: AddOne :: SubOne :: AddOne :: HNil)

      Then("the interpreter will generate events and update the state accordingly")

      val (persistedEvent2, state2, publishedEvent2) = EventSourcer(initialState, statement2)

      assert(persistedEvent2 === OneAdded :: OneAdded :: OneSubbed :: OneAdded :: HNil)
      assert(state2 === State(12))
      assert(publishedEvent2 === Some(Some(AdditionPerformed(1)) :: Some(AdditionPerformed(1)) :: Some(AdditionPerformed(
        -1)) :: Some(AdditionPerformed(1)) :: HNil))


      When("a simple program (a list with one single Statement) is given")

      val statement3 = Statement(SubOne :: HNil)

      Then("the interpreter will generate events and update the state accordingly")

      val (persistedEvent3, state3, publishedEvent3) = EventSourcer(initialState, statement3)

      assert(persistedEvent3 === OneSubbed :: HNil)
      assert(state3 === State(9))
      assert(publishedEvent3 === Some(Some(AdditionPerformed(-1)) :: HNil))
    }
  }
}
