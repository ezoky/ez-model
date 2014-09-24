package com.ezoky.ezmodel.core

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.ezoky.ezmodel.core.Atoms.{Name, Model}
import com.ezoky.ezmodel.core.Structures._
import com.ezoky.ezmodel.core.EzModel._
import com.ezoky.ezmodel.storage.EventStore

@RunWith(classOf[JUnitRunner])
class EntityTest extends FunSuite {

  test("Entity elaboration") {

    val offre = Entity("Offre") aggregate(multiple, Entity("Gamme") aggregate(multiple, Entity("Sous-Gamme") aggregate(multiple, "Prestation"))) attribute ("nom")

    assert(offre.name === Name("Offre"))

    assert(offre.attributes.size === 1)
    assert(offre.attributes(0).name === Name("nom"))

    assert(offre.aggregates.size === 1)
    assert(offre.aggregates(0).leaf.name === Name("Gamme"))
    assert(offre.aggregates(0).leaf.aggregates.size === 1)
    assert(offre.aggregates(0).leaf.aggregates(0).leaf.name === Name("Sous-Gamme"))
    assert(offre.aggregates(0).leaf.aggregates(0).leaf.aggregates.size === 1)
    assert(offre.aggregates(0).leaf.aggregates(0).leaf.aggregates(0).leaf.name === Name("Prestation"))

    assert(offre.references.size === 0)
  }
}