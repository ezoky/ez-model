package com.ezoky.ezmodel.control

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 02/04/2021
  * @since 0.2.0
  */
class ReflectionHelperTest
  extends AnyFunSuite {

  test("instantiating a case class") {

    import ReflectionHelperTest._

    assert(ReflectionHelper.instantiateWithArgs[NoDefaultArg](Seq("a", 2)) === Some(NoDefaultArg("a", 2)))
    assert(ReflectionHelper.instantiateWithArgs[NoDefaultArg](Seq("a")) === None)
    assert(ReflectionHelper.instantiateWithArgs[NoDefaultArg](Seq("a", "b")) === None)
    assert(ReflectionHelper.instantiateWithArgs[NoDefaultArg](Seq("a", 2, "extra arg")) === Some(NoDefaultArg("a", 2)))


    assert(ReflectionHelper.instantiateWithArgs[WithDefaultArgs](Seq("a", 2)) === Some(WithDefaultArgs("a", 2)))
    assert(ReflectionHelper.instantiateWithArgs[WithDefaultArgs](Seq("a")) === Some(WithDefaultArgs("a", 10)))
    assert(ReflectionHelper.instantiateWithArgs[WithDefaultArgs](Seq("a", "b")) === None)
    assert(ReflectionHelper.instantiateWithArgs[WithDefaultArgs](Seq("a", 2, "extra arg")) === Some(WithDefaultArgs("a", 2)))
  }

}

object ReflectionHelperTest {

  case class NoDefaultArg(a: String, b: Int)

  case class WithDefaultArgs(a: String, b: Int = 10)
}
