package com.ezoky.ezmodel.interaction.interpreter

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 21/02/2021
  * @since 0.2.0
  */
class ProcessingSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "the Processor" should {
    "be able to process a change of state entailed by a set of commands" in {

      object Processing extends Processing
      import Processing._

      Given("a state")
      case class Calculator(result: Int)


      And("a modelling syntax")
      case object iWantToAddOne
      case object iWantToAddTwo
      case object iWantToAddThree
      case object iWantToSubtractOne
      case class iWantToMultiplyBy(mul: Int)


      And("a set of statements")
      case class Add(i: Int)
      case class Subtract(i: Int)
      case class Multiply(by: Int)


      And("some parsing directives")
      implicit val addOneParser: Parser[iWantToAddOne.type, Add] =
        Parser.define(_ => Statement(Add(1)))
      implicit val addTwoParser: Parser[iWantToAddTwo.type, Add] =
        Parser.define(_ => Statement(Add(2)))
      implicit val addThreeParser: Parser[iWantToAddThree.type, Add] =
        Parser.define(_ => Statement(Add(3)))

      implicit val subParser: Parser[iWantToSubtractOne.type, Subtract] =
        Parser.define(_ => Statement(Subtract(1)))

      implicit val mulParser: Parser[iWantToMultiplyBy, Multiply] =
        Parser.define(s => Statement(Multiply(s.mul)))


      And("an interpreter for each statement")
      implicit val addInterpretor: Interpreter[Calculator, Add] =
        Interpreter.define { s => add => s.copy(result = s.result + add.i) }

      implicit val subInterpretor: Interpreter[Calculator, Subtract] =
        Interpreter.define { s => sub => s.copy(result = s.result - sub.i) }

      implicit val mulInterpretor: Interpreter[Calculator, Multiply] =
        Interpreter.define { s => mul => s.copy(result = s.result * mul.by) }


      When("i say something")
      val whatISay = Say(iWantToAddOne, iWantToAddTwo, iWantToAddThree, iWantToSubtractOne, iWantToMultiplyBy(7))


      Then("the processor can understand it compile int and change its state accordingly")
      val initialState = Calculator(-1)
      val newState = Processor(initialState).process(whatISay).state

      assert(newState === Calculator((-1 + 1 + 2 + 3 - 1) * 7))
    }
  }

}
