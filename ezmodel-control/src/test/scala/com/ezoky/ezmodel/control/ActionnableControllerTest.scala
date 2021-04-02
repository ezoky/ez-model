package com.ezoky.ezmodel.control

import com.ezoky.ezmodel.control.ActionnableController._
import org.scalatest.funsuite.AnyFunSuite
import shapeless.HNil

/**
  * @author gweinbach on 01/04/2021
  * @since 0.2.0
  */
class ActionnableControllerTest
  extends AnyFunSuite {

  test("noAction is the neutral controller") {

    import ActionnableControllerTest._

    val createCounter = InstantiateObject[Counter]()
    val noActionOnArgs = noAction[ArgsHolder]
    val noActionLeft = noActionOnArgs | createCounter
    val noActionRight = createCounter | noAction
    val noActionTwice = noActionOnArgs | noActionOnArgs

    val args = SingleValueHolder(10)
    val expectedResult = SingleValueHolder(Some(Counter(10)))

    assert(createCounter(args) === expectedResult)
    assert(noActionLeft(args) === expectedResult)
    assert(noActionRight(args) === expectedResult)
    assert(noActionOnArgs(args) === args)
    assert(noActionTwice(args) === args)
  }

  test("controllers can compose") {

    import ActionnableControllerTest._

    val createCounter = InstantiateObject[Counter]()
    val createCustomer = InstantiateObject[Customer]()
    val createInvoice = InstantiateObject[Invoice]()


    val workflow: Join[Int, ArgsHolder, Option[Invoice], SingleValueHolder[Option[Invoice]], String, ArgsHolder, Option[Customer], SingleValueHolder[Option[Customer]]] =
      Join(createCounter.map(_.map[Counter](_.get)) | createInvoice, createCustomer)

    val initialCounter = 10
    val customerName = "Groupe W"


    val args =
      MultipleValueHolder(initialCounter :: customerName :: HNil)
    val expectedResult =
      MultipleValueHolder(Some(Invoice(Counter(initialCounter))) :: Some(Customer(customerName)) :: HNil)

    assert(workflow(args) === expectedResult)
  }
}

object ActionnableControllerTest {

  case class Counter(count: Int)

  case class Customer(name: String)

  case class Invoice(number: Counter,
                     customer: Option[Customer] = None)

}
