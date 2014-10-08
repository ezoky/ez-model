package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EntityTest extends FunSuite {

  test("Entity elaboration") {

    val offre = Entity("Offre") aggregate(multiple, Entity("Gamme") aggregate(multiple, Entity("Sous-Gamme") aggregate(multiple, "Prestation"))) attribute ("nom")

    assert(offre.name === Name("Offre"))

    assert(offre.attributes.size === 1)
    assert(offre.attributes.head._1 === Name("nom"))

    assert(offre.aggregates.size === 1)
    assert(offre.aggregates.head._2.leaf.name === Name("Gamme"))
    assert(offre.aggregates.head._2.leaf.aggregates.size === 1)
    assert(offre.aggregates.head._2.leaf.aggregates.head._2.leaf.name === Name("Sous-Gamme"))
    assert(offre.aggregates.head._2.leaf.aggregates.head._2.leaf.aggregates.size === 1)
    assert(offre.aggregates.head._2.leaf.aggregates.head._2.leaf.aggregates.head._2.leaf.name === Name("Prestation"))

    assert(offre.references.size === 0)
  }
}