package com.ezoky.ezmodel.core

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
class ModelTest
  extends AnyFunSuite
    with StandardModels {

  test("A Model is a container for items partitioned into Domains") {

    val model =
      Model(Name("test project"))
        .withDomain(Domain(Name("Accounting")))
        .withDomain(Domain(Name("Finance")))
        .withDomain(Domain(Name("Accounting")))

    assert(model.domains.size === 2)
  }

}
