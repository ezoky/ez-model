package com.ezoky.ezmodel.core

import org.scalatest.funsuite.AnyFunSuite

class EntityTest
  extends AnyFunSuite
    with Entities {

  test("Range multiplicity is between a positive min and a max greater than min") {
    val range0_0 = range(0, 0)
    assert((range0_0.min, range0_0.max) === (0, 0))

    val range1_1 = range(0, 0)
    assert((range1_1.min, range1_1.max) === (0, 0))

    val range1_3 = range(1, 3)
    assert((range1_3.min, range1_3.max) === (1, 3))

    val range3_1 = range(3, 1)
    assert((range3_1.min, range3_1.max) === (1, 3))

    val range_1_3 = range(-1, 3)
    assert((range_1_3.min, range_1_3.max) === (0, 3))

    val range1__3 = range(1, -3)
    assert((range1__3.min, range1__3.max) === (0, 1))

    val range_1__3 = range(-1, -3)
    assert((range_1__3.min, range_1__3.max) === (0, 0))
  }
  
  test("Entity elaboration") {

    val offre =
      Entity(Name("Offre"))
        .withAggregate(
          Name("gammes de fabrication"),
          Entity(Name("Gamme"))
            .withAggregate(
              Name("sous-gammes"),
              Entity(Name("Sous-Gamme"))
                .withAggregate(
                  Name("prestations vendues"),
                  Entity(Name("Prestation")),
                  multiple
                ),
              multiple
            ),
          multiple
        )
        .withAttribute(Name("nom"))

    assert(offre.name === Name("Offre"))

    assert(offre.attributes.size === 1)
    assert(offre.attributes.head._1 === Name("nom"))

    assert(offre.aggregated.size === 1)
    assert(offre.aggregated.head._2.name === Name("gammes de fabrication"))
    assert(offre.aggregated.head._2.leaf.name === Name("Gamme"))
    assert(offre.aggregated.head._2.leaf.aggregated.size === 1)
    assert(offre.aggregated.head._2.leaf.aggregated.head._2.leaf.name === Name("Sous-Gamme"))
    assert(offre.aggregated.head._2.leaf.aggregated.head._2.leaf.aggregated.size === 1)
    assert(offre.aggregated.head._2.leaf.aggregated.head._2.leaf.aggregated.head._2.leaf.name === Name("Prestation"))

    assert(offre.referenced.size === 0)
  }
}