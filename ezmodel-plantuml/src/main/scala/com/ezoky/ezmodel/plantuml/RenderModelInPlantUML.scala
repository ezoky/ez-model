package com.ezoky.ezmodel.plantuml

import cats._
import cats.implicits._
import com.ezoky.architecture._
import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezplantuml.{PlantUMLDiagram, PlantUMLWrapper, SVGString}

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
trait RenderModelInPlantUMLAPI[QueryProducing[_]] {

  @Query
  def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]]

  @Query
  def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]]
}

case class ModellingDiagram(plantUMLDiagram: PlantUMLDiagram,
                            svg: Option[SVGString])


class RenderModelInPlantUML[QueryProducing[_] : Monad](plantUMLService: PlantUMLWrapper[QueryProducing])
  extends RenderModelInPlantUMLAPI[QueryProducing]
    with PlantUMLModelRendering {

  override def generateSVG(model: Model): QueryProducing[Set[ModellingDiagram]] = {
    for {
      setOfPlantUMLDiagrams <- generateDiagrams(model)
      listOfModellingDiagrams <- {
        setOfPlantUMLDiagrams.toList.map {
          diagram =>
            plantUMLService.diagramSVG(diagram).map {
              svgString =>
                ModellingDiagram(
                  diagram,
                  svgString
                )
            }
        }
      }.traverse(identity)
    } yield listOfModellingDiagrams.toSet
  }


  override def generateDiagrams(model: Model): QueryProducing[Set[PlantUMLDiagram]] =
    Monad[QueryProducing].pure(
      for {
        domain <- model.domains.values.toSet[Domain]
        plantUMLDiagram <- PlantUMLModelRenderer[Domain, PlantUMLDiagram].renderUML(domain)
      } yield plantUMLDiagram
    )
}

object RenderModelInPlantUML {
  def apply[QueryProducing[_] : Monad : PlantUMLWrapper: RenderModelInPlantUML]: RenderModelInPlantUML[QueryProducing] =
    implicitly[RenderModelInPlantUML[QueryProducing]]
}
