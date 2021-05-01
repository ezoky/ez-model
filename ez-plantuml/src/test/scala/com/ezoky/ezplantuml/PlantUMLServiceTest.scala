package com.ezoky.ezplantuml

import com.ezoky.architecture.zioapi.ZIOAPI
import org.scalatest.funsuite.AnyFunSuite
import zio._

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
class PlantUMLServiceTest
  extends AnyFunSuite {

  test("generating PlantUML source from a use case diagram") {

    val result =
      Runtime.default.unsafeRun(ZIOPlantUMLService.diagramSource(PlantUMLTestFixture.useCaseDiagram))
    assert(result === Some(PlantUMLTestFixture.useCaseDiagramSrc))
  }

  test("generating SVG from a use case diagram") {

    def filterSVG(svg: SVGString): SVGString =
      svg.map(
        _.replaceAll("""id=".*?"""", """id="ID"""")
          .replaceAll("""url\(#.*?\)""", """url(#ID)""")
      )

    val result =
      Runtime.default.unsafeRun(ZIOPlantUMLService.diagramSVG(PlantUMLTestFixture.useCaseDiagram).map(_.map(filterSVG)))
    assert(result === Some(filterSVG(PlantUMLTestFixture.useCaseDiagramSVG)))
  }
}


object ZIOPlantUMLService
    extends PlantUMLService(ZIOAPI)
