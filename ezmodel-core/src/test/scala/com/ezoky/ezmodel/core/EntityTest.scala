package com.ezoky.ezmodel.core

import org.scalatest.funsuite.AnyFunSuite

class EntityTest
  extends AnyFunSuite
    with Entities {

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