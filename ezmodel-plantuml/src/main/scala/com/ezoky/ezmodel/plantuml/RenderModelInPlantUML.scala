package com.ezoky.ezmodel.plantuml

import cats.implicits._
import com.ezoky.architecture._
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezplantuml.{PlantUMLDiagram, PlantUMLServiceAPI, SVGString}

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
trait RenderModelInPlantUMLAPI[A <: API] {

  val api: A

  import api._

  @Query
  def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]]

  @Query
  def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]]
}

case class ModellingDiagram(plantUMLDiagram: PlantUMLDiagram,
                            svg: Option[SVGString])


class RenderModelInPlantUML[A <: API](plantUMLService: PlantUMLServiceAPI[A])(override val api: A)
  extends RenderModelInPlantUMLAPI[A]
    with PlantUMLModelRendering {

  import api._

  override def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]] = {
    for {
      setOfPlantUMLDiagrams <- generateDiagrams(model)
      setOfModellingDiagrams <- api.validate {
        setOfPlantUMLDiagrams.toList.map {
          diagram =>
            import plantUMLService.api._
            plantUMLService.diagramSVG(diagram).map {
              svgString =>
                ModellingDiagram(
                  diagram,
                  svgString
                )
            }
        }
      }
    } yield setOfModellingDiagrams.toSet
  }

  //  generateDiagrams(model).map(plantUmlDiagramSet =>
  //    plantUmlDiagramSet.foldLeft(Set.empty[ModellingDiagram])((set, diagram) =>
  //      set + ModellingDiagram(
  //        diagram,
  //        plantUMLService.diagramSVG(diagram)
  //      )
  //    )


  override def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]] =
    queryMonad.pure(
      for {
        domain <- model.domains.values.toSet
        plantUMLDiagram <- PlantUMLModelRenderer[Domain, PlantUMLDiagram].renderUML(domain)
      } yield plantUMLDiagram
    )
}

//object SimpleRenderModelInPlantUML
//  extends RenderModelInPlantUMLImpl(SimplePlantUMLService)
//    with monolithic.MonolithicAPI