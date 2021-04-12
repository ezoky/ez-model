package com.ezoky.ezmodel.plantuml

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezplantuml.PlantUMLService.SVGString
import com.ezoky.ezplantuml.{PlantUMLDiagram, PlantUMLService, SimplePlantUMLService}

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
trait PlantUMLRenderingService {

  def generateDiagrams(model: Model): Set[PlantUMLDiagram]

  def generateSVG(model: Model): Set[ModellingDiagram]
}

case class ModellingDiagram(plantUMLDiagram: PlantUMLDiagram,
                            svg: Option[SVGString])

class SimplePlantUMLRenderingService(plantUMLService: PlantUMLService)
  extends PlantUMLRenderingService
    with PlantUMLModelRendering {

  override def generateSVG(model: Model): Set[ModellingDiagram] =
    generateDiagrams(model).foldLeft(Set.empty[ModellingDiagram])((set, diagram) =>
      set + ModellingDiagram(
        diagram,
        plantUMLService.diagramSVG(diagram)
      )
    )

  override def generateDiagrams(model: Model): Set[PlantUMLDiagram] =
    model.domains.values.foldLeft(Set.empty[PlantUMLDiagram])((set, domain) =>
      set ++ PlantUMLModelRenderer[Domain, PlantUMLDiagram].renderUML(domain)
    )
}

object SimplePlantUMLRenderingService
  extends SimplePlantUMLRenderingService(SimplePlantUMLService)