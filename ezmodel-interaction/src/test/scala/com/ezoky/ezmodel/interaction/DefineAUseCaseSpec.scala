package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezmodel.interaction.dsl.DSL._
import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 04/02/2021
  * @since 0.2.0
  */
class DefineAUseCaseSpec
  extends AnyWordSpec
   with GivenWhenThen {

  import Modelling._

  "the Interpreter" when {
    "the Modeller defines a Use Case" should {
      "add it to the current model" in {

        val iSay =
          Say {
            asAn ("Accountant") iWantTo ("invoice" a "Month") provided ("Production" is "Done") resultingIn ("Current Month" is "Invoiced")
          }

        val definedUseCase =
          UseCase(
            Actor(Name("Accountant")),
            Goal(Action(Verb("invoice")), Some(ActionObject(NameGroup(Determinant.a, Name("Month"))))),
            Constraints(
              Pre -> EntityState(Entity(Name("Production")), StateName(Qualifier("Done"))),
              Post -> EntityState(Entity(Name("Current Month")), StateName(Qualifier("Invoiced")))
            )
          )

        assert(CurrentModel contains definedUseCase)
      }
    }
  }
}
