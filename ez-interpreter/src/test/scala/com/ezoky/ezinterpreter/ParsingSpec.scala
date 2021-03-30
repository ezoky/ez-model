package com.ezoky.ezinterpreter

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec
import shapeless.HNil

/**
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
class ParsingSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "the Modeller" should {
    "be able to provide parsable modelling statements" in {

      // Modelling activity is restricted to Parsing
      object Parsing extends Saying with Parsing
      import Parsing._


      Given("a modelling syntax")

      case object someModellingStatement
      case object aModellingStatement
      case class anotherModellingStatement(arg: String)


      And("some parsing directives")

      implicit val someParser: Parser[someModellingStatement.type , String] =
        Parser.define(_ => Statement("statement 1"))

      implicit val aParser: Parser[aModellingStatement.type , String] =
        Parser.define(_ => Statement("statement 2"))

      implicit val anotherParser: Parser[anotherModellingStatement , (String, String)] =
        Parser.define(s => Statement(("statement 3", s.arg)))


      When("the modeller declares a statement")

      val statements1 =
        Say {
          someModellingStatement
        }


      Then("the parser understands it")

      val parsed1 = Parser(statements1)
      assert(parsed1.stated === "statement 1")


      When("the modeller declares multiple statements")

      val statements2 =
        Say(
          aModellingStatement,
          anotherModellingStatement("with an argument")
        )


      Then("the parser understands them")
      
      val parsed2 = Parser(statements2)
      assert(parsed2.stated === "statement 2" :: ("statement 3", "with an argument") :: HNil)
    }
  }
}
