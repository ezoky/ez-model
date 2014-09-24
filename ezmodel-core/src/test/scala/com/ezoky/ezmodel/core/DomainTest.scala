package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.Atoms.Model
import com.ezoky.ezmodel.core.EzModel._
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.storage.EventStore
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * @author gweinbach
 */
@RunWith(classOf[JUnitRunner])
class DomainTest extends FunSuite {

  test("Domain elaboration") {

    val dom = Domain("Driving") useCase("Driver", "change", "Gear") useCase("Driver", "brake") entity("Gear")

    assert(dom.useCases.size === 2)
    assert(dom.entities.size === 1)
  }
}
