package com.ezmodel.core

import com.ezmodel.core.Domains.Domain
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class DomainTest extends FunSuite {

  test("Domain elaboration") {

    val dom = Domain("Driving") useCase("Driver", "change", "Gear") useCase("Driver", "brake") entity ("Gear")

    assert(dom.useCases.size === 2)
    assert(dom.entities.size === 1)
  }
}
