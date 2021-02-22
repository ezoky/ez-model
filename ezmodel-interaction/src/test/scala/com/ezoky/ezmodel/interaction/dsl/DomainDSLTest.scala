package com.ezoky.ezmodel.interaction.dsl

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach
  */
class DomainDSLTest extends AnyFunSuite {

  import com.ezoky.ezmodel.core.Models._

  object Say extends AtomsDSL with DomainDSL with UseCaseDSL with EntityDSL

  import Say._

  test("Domain elaboration") {

    val dom = Domain("Driving") withUseCase ("Driver", "change" the "Gear") withUseCase ("Driver", "brake") withEntity "Gear"

    assert(dom.useCases.size === 2)
    assert(dom.entities.size === 1)
  }
}
