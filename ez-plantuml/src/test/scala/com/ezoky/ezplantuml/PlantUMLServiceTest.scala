package com.ezoky.ezplantuml

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
class PlantUMLServiceTest
  extends AnyFunSuite {

  test("generating PlantUML source from a use case diagram") {

    assert(SimplePlantUMLService.diagramSource(PlantUMLTestFixture.useCaseDiagram) ===
           Some(PlantUMLTestFixture.useCaseDiagramSrc))
  }

  test("generating SVG from a use case diagram") {

    def filterSVG(svg: String): String =
      svg
        .replaceAll("""id=".*?"""", """id="ID"""")
        .replaceAll("""url\(#.*?\)""", """url(#ID)""")

    assert(SimplePlantUMLService.diagramSVG(PlantUMLTestFixture.useCaseDiagram).map(filterSVG) ===
           Some(filterSVG(PlantUMLTestFixture.useCaseDiagramSVG)))
  }
}