package com.ezoky.ezmodel.control

import org.scalatest.GivenWhenThen
import org.scalatest.wordspec.AnyWordSpec

/**
  * @author gweinbach on 31/03/2021
  * @since 0.2.0
  */
class SingleRelationControlSpec
  extends AnyWordSpec
    with GivenWhenThen {

  "Single relation Setter" should {
    "be used to uodate a relation value of an existing object" in {

      Given("a model")
      import SingleRelationControlSpec.EntityWithOneArg
      case class MainObject(anEntity: EntityWithOneArg)

      Given("and a controller")
      val controller = SetSingleRelation[MainObject, EntityWithOneArg](_.anEntity)

      Given("and some existing values")
      val initialValue = EntityWithOneArg("initial entity")
      val existingMain = MainObject(initialValue)

      When("the controller is actionned with new values")
      val newValue = EntityWithOneArg("new entity")
      val inputValues = SingleValueHolder(newValue)
      val outputValues = controller(existingMain, inputValues)

      Then("the relation is updated")
      assert(outputValues.value === MainObject(newValue))
    }
  }

  "Single object instanciator" should {
    "be used to instantiate an object with one argument constructor" in {

      Given("a model")
      import SingleRelationControlSpec.EntityWithOneArg

      Given("and some instantiation controller")
      val controller = InstantiateObject[EntityWithOneArg]()

      When("the controller is actionned with constructor parameters values")
      val inputValues = SingleValueHolder[String]("this is mandatory")
      val outputValues = controller(inputValues)

      Then("a new entity is created and the relation is updated")
      assert(outputValues.value === Some(EntityWithOneArg("this is mandatory")))
    }
    "be used to instantiate an object with two arguments constructor" in {

      Given("a model")
      import SingleRelationControlSpec.EntityWithTwoArgs

      Given("and some instantiation controller")
      val controller = InstantiateObject[EntityWithTwoArgs]()

      When("the controller is actionned with constructor parameters values")
      val inputValues = MultipleValueHolder("this is mandatory", 10)
      val outputValues = controller(inputValues)

      Then("a new entity is created and the relation is updated")
      assert(outputValues.value === Some(EntityWithTwoArgs("this is mandatory", 10)))
    }
  }

  "Single relation instanciator" should {
    "be used to set a new Entity instance to an optional relation of an existing object" in {

      Given("a model")
      import SingleRelationControlSpec.EntityWithOneArg
      case class MainObjectWithOption(anEntity: Option[EntityWithOneArg] = None)

      Given("and a controller")
      val controller = InstantiateAndSetSingleOptionalRelation[MainObjectWithOption, EntityWithOneArg](_.anEntity)

      Given("and some existing values")
      val existingMain = MainObjectWithOption()

      When("the controller is actionned with new values")
      val inputValues = SingleValueHolder[String]("this is mandatory")
      val outputValues = controller(existingMain, inputValues)

      Then("a new entity is created and the relation is updated")
      assert(outputValues.value === MainObjectWithOption(Some(EntityWithOneArg("this is mandatory"))))
    }
  }
}

/**
  * There is no TypeTag for inner classes of Classes or Traits, and we need some TypeTag for those classes
  */
object SingleRelationControlSpec {

  case class EntityWithOneArg(mandatoryData: String)
  case class EntityWithTwoArgs(mandatoryData: String,
                               anotherMandatoryDate: Int)

}