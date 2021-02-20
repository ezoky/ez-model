package com.ezoky.ezmodel.interaction.dsl

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 17/02/2021
  * @since 0.2.0
  */
class AtomsDSLTest
  extends AnyFunSuite {

  import com.ezoky.ezmodel.core.Models.{Name, NameGroup, Determinant}
  object Say extends AtomsDSL
  import Say._

  test("strings can be seen as atoms depending on the (implicit) context") {

    val aThing = NameGroup(Determinant.a, Name("thing"))

//    val aTupleThing: NameGroup = (a, "thing")
//    assert(aTupleThing === aThing)

    val aFunctionThing = Say a("thing")
    assert(aFunctionThing === aThing)

    val aFluentThing = Say a "thing"
    assert(aFluentThing === aThing)
  }

}
