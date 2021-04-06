package com.ezoky.ezmodel.plantuml

/**
  * @author gweinbach on 05/04/2021
  * @since 0.2.0
  */
sealed trait PlantUML

sealed trait PlantUMLNode
  extends PlantUML {
  val name: String
}

sealed trait PlantUMLContainer
  extends PlantUML {
  val packages: Set[PlantUMLPackage]
  val actors: Set[PlantUMLActor]
  val useCases: Set[PlantUMLUseCase]
}

final case class PlantUMLDiagram(packages: Set[PlantUMLPackage] = Set.empty,
                                 actors: Set[PlantUMLActor] = Set.empty,
                                 useCases: Set[PlantUMLUseCase] = Set.empty)
  extends PlantUMLContainer

case class PlantUMLPackage(name: String,
                           packages: Set[PlantUMLPackage] = Set.empty,
                           actors: Set[PlantUMLActor] = Set.empty,
                           useCases: Set[PlantUMLUseCase] = Set.empty)
  extends PlantUMLContainer
    with PlantUMLNode

final case class PlantUMLUseCase(name: String)
  extends PlantUMLNode

final case class PlantUMLActor(name: String)
  extends PlantUMLNode


