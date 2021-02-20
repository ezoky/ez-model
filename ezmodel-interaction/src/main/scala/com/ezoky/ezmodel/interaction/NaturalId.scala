package com.ezoky.ezmodel.interaction


/**
  * The `NaturalId` of an item is the natural way for a user to identify (name) an item.
  *
  * @author gweinbach on 20/02/2021
  * @since 0.2.0
  */
trait NaturalId[T] {

  type IdType

  def id(t: T): IdType
}

object NaturalId {

  private def define[T, I](naturalId: T => I): NaturalId[T] =
    new NaturalId[T] {

      override type IdType = I

      override def id(t: T): I =
        naturalId(t)
    }

  trait TypeClasses {

    import com.ezoky.ezmodel.core.Models._

    implicit val DomainNaturalId: NaturalId[Domain] =
      define(_.name)

    implicit val ModelNaturalId: NaturalId[Model] =
      define(_.name)

    implicit val EntityNaturalId: NaturalId[Entity] =
      define(_.name)

    implicit val ActorNaturalId: NaturalId[Actor] =
      define(_.name)

    implicit val GoalId: NaturalId[Goal] =
      define { goal =>
        (goal.action.verb, goal.actionObject.map { actionObject =>
          (actionObject.nameGroup.determinant, actionObject.nameGroup.name)
        })
      }

    implicit def UseCaseNaturalId(implicit
                                  actorId: NaturalId[Actor],
                                  goalId: NaturalId[Goal]): NaturalId[UseCase] =
      define { useCase =>
        (actorId.id(useCase.actor), goalId.id(useCase.goal))
      }
  }
}