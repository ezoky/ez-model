package com.ezoky.ezmodel.interaction

import com.ezoky.ezmodel.core.Atoms.{Name, NameGroup, Qualifier, Verb, a}
import com.ezoky.ezmodel.core.Constraints
import com.ezoky.ezmodel.core.Constraints.{Post, Pre}
import com.ezoky.ezmodel.core.Entities.{Entity, EntityState, StateName}
import com.ezoky.ezmodel.core.UseCases.{Action, ActionObject, Actor, Goal, UseCase, asAn}
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author gweinbach on 04/02/2021
 * @since 0.2.0
 */
class DefineAUseCaseSpec
  extends AnyWordSpec {

  "the interpretor" when {
    "the user defines a Use Case" should {
      "add it to the current model" in {
        Say {
          asAn("Accountant") iWantTo("invoice", (a, "Month")) preCondition EntityState("Production", "Done") postCondition EntityState("Current Month", "Invoiced")
        }

        val definedUseCase =
          UseCase(
            Actor(Name("Accountant")),
            Goal(Action(Verb("invoice")), ActionObject(NameGroup(a, Name("Month")))),
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
