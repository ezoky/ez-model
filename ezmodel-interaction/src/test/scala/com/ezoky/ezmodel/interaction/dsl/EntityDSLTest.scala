package com.ezoky.ezmodel.interaction.dsl

import org.scalatest.funsuite.AnyFunSuite

class EntityDSLTest extends AnyFunSuite {

  import com.ezoky.ezmodel.core.Models._

  object Say extends AtomsDSL with EntityDSL

  import Say._

  test("Entity elaboration") {

    val expectedEntity =
      Entity(
        Name("Offre"),
        attributes = Map(
          Name("Nom") -> Attribute(Name("Nom"), single, false)
        ),
        aggregated = Map(
          Name("Gamme") ->
            Aggregate(
              root = Entity(Name("Offre")),
              Name("Gamme"),
              Entity(
                Name("Gamme") ,
                aggregated = Map(
                  Name("Sous-Gamme") ->
                  Aggregate(
                    root = Entity(Name("Gamme")),
                    Name("Sous-Gamme"),
                    Entity(
                      Name("Sous-Gamme"),
                      aggregated = Map(
                        Name("Gamme") ->
                        Aggregate(
                          root = Entity(Name("Sous-Gamme")),
                          Name("prestations vendues"),
                          Entity(
                            Name("Prestation")
                          ), multiple, false)
                      )
                    ), multiple, false)
                )
              ), multiple, false)
        )
      )

    val offre = Entity("Offre") aggregates many (Entity("Gamme") aggregates many (Entity("Sous-Gamme") aggregates many (Entity("Prestation")) as "prestations vendues")) withAttribute "nom"

    assert(offre === expectedEntity)
  }
}