package com.ezoky.ezmodel.plantuml

import com.ezoky.architecture._
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezplantuml.{PlantUMLDiagram, PlantUMLServiceAPI, SVGString}

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
trait RenderModelInPlantUMLAPI
  extends API {

  @Query
  def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]]

  @Query
  def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]]
}

case class ModellingDiagram(plantUMLDiagram: PlantUMLDiagram,
                            svg: Option[SVGString])


class RenderModelInPlantUML(plantUMLService: PlantUMLServiceAPI)
  extends RenderModelInPlantUMLAPI
    with PlantUMLModelRendering {


  override def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]] = {
    for {
      setOfPlantUMLDiagrams <- generateDiagrams(model)
      setOfModellingDiagrams <- ZIO.validate {
          setOfPlantUMLDiagrams.map(diagram =>
             plantUMLService.diagramSVG(diagram).map {
                svgString =>
                  ModellingDiagram(
                    diagram,
                    svgString
                  )
          })
      }(identity)
    } yield setOfModellingDiagrams
  }

  //  generateDiagrams(model).map(plantUmlDiagramSet =>
  //    plantUmlDiagramSet.foldLeft(Set.empty[ModellingDiagram])((set, diagram) =>
  //      set + ModellingDiagram(
  //        diagram,
  //        plantUMLService.diagramSVG(diagram)
  //      )
  //    )


  override def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]] =
    model.domains.values.foldLeft(Set.empty[PlantUMLDiagram])((set, domain) =>
      set ++ PlantUMLModelRenderer[Domain, PlantUMLDiagram].renderUML(domain)
    )
}

//object SimpleRenderModelInPlantUML
//  extends RenderModelInPlantUMLImpl(SimplePlantUMLService)
//    with monolithic.MonolithicAPI