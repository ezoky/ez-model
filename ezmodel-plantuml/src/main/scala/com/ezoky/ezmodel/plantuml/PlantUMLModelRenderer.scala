package com.ezoky.ezmodel.plantuml

import com.ezoky.ezmodel.core.Models._
import com.ezoky.ezplantuml._

/**
  * @author gweinbach on 07/04/2021
  * @since 0.2.0
  */
trait PlantUMLModelRenderer[T, U <: PlantUML] {

  def renderUML(t: T): Set[U]

}

object PlantUMLModelRenderer {

  def apply[T, U <: PlantUML](implicit renderer: PlantUMLModelRenderer[T, U]): PlantUMLModelRenderer[T, U] =
    implicitly[PlantUMLModelRenderer[T, U]]

  def define[T, U <: PlantUML](renderer: T => Set[U]): PlantUMLModelRenderer[T, U] =
    t => renderer(t)
}

trait PlantUMLModelRendering {

  implicit def nameToString(name: Name): String =
    name.name

  implicit def verbToString(verb: Verb): String =
    verb.verb

  implicit def actorToString(actor: Actor): String =
    actor.name.name

  implicit def goalToString(goal: Goal): String =
    goal.action.verb.verb + goal.actionObject.fold("")(actionObject =>
      " " + actionObject.nameGroup.determinant.determinant + " " + actionObject.nameGroup.name.name
    )

  implicit val DomainPlantUMLRenderer: PlantUMLModelRenderer[Domain, PlantUMLDiagram] =
    PlantUMLModelRenderer.define {
      domain =>
        if (domain.useCases.isEmpty) {
          Set.empty[PlantUMLDiagram]
        }
        else {
          Set(
            PlantUMLDiagram(
              packages = Set(
                PlantUMLPackage(
                  name = domain.name,
                  actors = domain.useCases.values.foldLeft(Set.empty[PlantUMLActor])((set, uc) =>
                    set + PlantUMLActor(uc.actor)
                  ),
                  useCases = domain.useCases.values.foldLeft(Set.empty[PlantUMLUseCase])((set, uc) =>
                    set + PlantUMLUseCase(uc.goal)
                  ),
                  relations = domain.useCases.values.foldLeft(Set.empty[PlantUMLRelation])((set, uc) =>
                    set + PlantUMLOrientedLink(
                      PlantUMLActor(uc.actor),
                      PlantUMLUseCase(uc.goal)
                    )
                  )
                )
              )
            )
          )
        }
    }
}