package com.ezoky.ezmodel.core

/**
  * Type class.
  *
  * The `NaturalId` of an item provides a way to identify (name) an item.
  * This Id should be unique in the context of the aggregate it is related to since this Id might be used to used as
  * a unique key in Maps.
  *
  * Some common type classes (see [[com.ezoky.ezmodel.core.NaturalId.StandardTypeClasses]]) are provided for default
  * usage.
  * Import them in the context of use:
  * {{{
  * object MyTypeClasses extends com.ezoky.ezmodel.core.NaturalId.StandardTypeClasses
  * import MyTypeClasses._
  * }}}
  *
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
trait NaturalId[T] {

  type IdType

  def apply(t: T): IdType
}

object NaturalId {

  type Aux[T, I] = NaturalId[T] { type IdType = I }

  private def define[T, I](naturalId: T => I): NaturalId[T] =
    new NaturalId[T] {

      override type IdType = I

      override def apply(t: T): I =
        naturalId(t)
    }

  type Dictionary[T, I <: NaturalId[T]] = Map[I#IdType, T]

  object Dictionary {

    def empty[T, I <: NaturalId[T]]: Dictionary[T, I] =
      Map.empty[I#IdType, T]

    def apply[T, I <: NaturalId[T]](points: T*)
                                   (implicit
                                    id: I): Dictionary[T, I] =
      points.foldLeft(empty[T, I])((map, point) => map + (id(point) -> point))
  }

  /**
    * Can be used for standard usages
    */
  trait StandardTypeClasses {

    import com.ezoky.ezmodel.core.Models._

    implicit val DomainNaturalId: NaturalId[Domain] =
      define(_.name)

    implicit val ModelNaturalId: NaturalId[Model] =
      define(_.name)

    implicit val EntityNaturalId: NaturalId[Entity] =
      define(_.name)

    implicit val ActorNaturalId: NaturalId[Actor] =
      define(_.name)

    implicit val GoalNaturalId: NaturalId[Goal] =
      define { goal =>
        (goal.action.verb, goal.actionObject.map { actionObject =>
          (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
        })
      }

    implicit def UseCaseNaturalId(implicit
                                  actorId: NaturalId[Actor],
                                  goalId: NaturalId[Goal]): NaturalId[UseCase] =
      define { useCase =>
        (actorId.apply(useCase.actor), goalId.apply(useCase.goal))
      }
  }
}