package com.ezoky.ezinterpreter

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
class ProcessingSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "the StateProcessor" should {
    "be able to process a change of state entailed by a set of commands" in {

      object Processing extends Processing
      import Processing._

      Given("a state")
      case class Calculator(result: Int)


      And("a modelling syntax")
      case object inOrderToToAddOne
      case object inOrderToAddTwo
      case object inOrderToAddThree
      case object inOrderToSubtractOne
      case class inOrderToMultiplyBy(mul: Int)


      And("a set of statements")
      case class Add(i: Int)
      case class Subtract(i: Int)
      case class Multiply(by: Int)


      And("some parsing directives")
      implicit val addOneParser: Parser[inOrderToToAddOne.type, Add] =
        Parser.define(_ => Statement(Add(1)))
      implicit val addTwoParser: Parser[inOrderToAddTwo.type, Add] =
        Parser.define(_ => Statement(Add(2)))
      implicit val addThreeParser: Parser[inOrderToAddThree.type, Add] =
        Parser.define(_ => Statement(Add(3)))

      implicit val subParser: Parser[inOrderToSubtractOne.type, Subtract] =
        Parser.define(_ => Statement(Subtract(1)))

      implicit val mulParser: Parser[inOrderToMultiplyBy, Multiply] =
        Parser.define(s => Statement(Multiply(s.mul)))


      And("an interpreter for each statement")
      implicit val addInterpretor: StateTransitionInterpreter[Calculator, Add] =
        StateTransitionInterpreter.define { s => add => s.copy(result = s.result + add.i) }

      implicit val subInterpretor: StateTransitionInterpreter[Calculator, Subtract] =
        StateTransitionInterpreter.define { s => sub => s.copy(result = s.result - sub.i) }

      implicit val mulInterpretor: StateTransitionInterpreter[Calculator, Multiply] =
        StateTransitionInterpreter.define { s => mul => s.copy(result = s.result * mul.by) }


      When("i say something")
      val whatISay = Say(inOrderToToAddOne, inOrderToAddTwo, inOrderToAddThree, inOrderToSubtractOne, inOrderToMultiplyBy(7))


      Then("the processor can understand it, compiles int and changes its state accordingly")
      val initialState = Calculator(-1)
      val newState = StateProcessor(initialState).process(whatISay).state

      assert(newState === Calculator((-1 + 1 + 2 + 3 - 1) * 7))
    }
  }

}
