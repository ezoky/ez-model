package com.ezoky.ezplantuml

import org.scalatest.funsuite.AnyFunSuite
import PlantUMLRenderers._

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
class PlantUMLRendererTest
  extends AnyFunSuite {

  test("rendering a use case diagram") {
    
    assert(PlantUMLTestFixture.useCaseDiagram.render() === PlantUMLTestFixture.useCaseDiagramSrc)
  }
}
