package com.ezoky.ezinterpreter

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec
import shapeless.HNil

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
class InterpretingSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "the Interpreter" should {
    "be able to update a state given a list of statements" in {

      // Modelling activity is restricted to Parsing
      object Interpreting extends Interpreting
      import Interpreting._

      case class State(total: Int)

      Given("a language syntax")

      case object AddOne
      case object SubOne


      And("some interpreters directives")

      implicit val addOneInterpretor: StateTransitionInterpreter[State, AddOne.type] =
        StateTransitionInterpreter.define { s => _ => s.copy(total = s.total + 1) }

      implicit val subOneInterpretor: StateTransitionInterpreter[State, SubOne.type] =
        StateTransitionInterpreter.define { s => _ => s.copy(total = s.total - 1) }

      And("a defined initial state ")

      val initialState = State(10)


      When("a statement is given")

      val statement1 = Statement(AddOne)

      Then("the interpreter will update the state accordingly")

      val state1 = Interpreter(initialState, statement1)

      assert(state1 === State(11))



      When("a program (a list of Statements) is given")

      val statement2 = Statement(AddOne :: AddOne :: SubOne :: AddOne :: HNil)

      Then("the interpreter will update the state accordingly")

      val state2 = Interpreter(initialState, statement2)

      assert(state2 === State(12))

    }
  }
}
