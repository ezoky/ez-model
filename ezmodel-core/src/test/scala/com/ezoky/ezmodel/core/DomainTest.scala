package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.Domains.Domain
import org.scalatest.funsuite.AnyFunSuite
/**
 * @author gweinbach
 */
class DomainTest extends AnyFunSuite {

  test("Domain elaboration") {

    val dom = Domain("Driving") useCase("Driver", "change", "Gear") useCase("Driver", "brake") entity ("Gear")

    assert(dom.useCases.size === 2)
    assert(dom.entities.size === 1)
  }
}
