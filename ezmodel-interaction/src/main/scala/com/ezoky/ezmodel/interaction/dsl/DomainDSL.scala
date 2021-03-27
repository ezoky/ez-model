package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._


/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
trait DomainDSL
  extends NaturalIdDSL
    with MergerDSL {

  implicit def stringToDomain(domainName: String): Domain =
    Domain(Name(domainName))

  def inDomain(domainName: String): Domain =
    Domain(Name(domainName))

  implicit class DomainHelper(domain: Domain) {

    def theEntity(name: String): Domain =
      domain.withEntity(Entity(Name(name)))

    def asA(actorName: Name): FluentUseCase =
      FluentUseCase(domain, Actor(actorName))

    def asAn(actorName: Name): FluentUseCase = asA(actorName)

    case class FluentUseCase(domain: Domain,
                             actor: Actor) {

      def inOrderTo(goal: Goal): UseCaseInDomainHelper =
        UseCaseInDomainHelper(domain, UseCase(actor, goal))

      // non transitive Action
      def inOrderTo(action: Action): UseCaseInDomainHelper =
        UseCaseInDomainHelper(domain, UseCase(actor, Goal(action, None)))

      // transitive Action
      def inOrderTo(action: Action,
                    actionObject: ActionObject): UseCaseInDomainHelper =
        UseCaseInDomainHelper(domain, UseCase(actor, Goal(action, Some(actionObject))))

      // transitive Action made simple
      def inOrderTo(action: Action,
                    determinant: Determinant,
                    name: Name): UseCaseInDomainHelper =
        UseCaseInDomainHelper(domain, UseCase(actor, Goal(action, Some(ActionObject(NameGroup(determinant, name))))))
    }

  }

  case class UseCaseInDomainHelper(domain: Domain,
                                   useCase: UseCase) {
    def toDomain: Domain =
      domain.withUseCase(useCase)

    def iWantTo(interaction: Interaction): UseCaseInDomainHelper =
      UseCaseInDomainHelper(
        domain,
        useCase.withInteraction(interaction)
      )

    // non transitive Action
    def iWantTo(action: Action): UseCaseInDomainHelper =
      UseCaseInDomainHelper(
        domain,
        useCase.withInteraction(Interaction(action, None))
      )

    // transitive Action
    def iWantTo(action: Action,
                actionObject: ActionObject): UseCaseInDomainHelper =
      UseCaseInDomainHelper(
        domain,
        useCase.withInteraction(Interaction(action, Some(actionObject)))
      )

    // transitive Action made simple
    def iWantTo(action: Action,
                determinant: Determinant,
                name: Name): UseCaseInDomainHelper =
      UseCaseInDomainHelper(
        domain,
        useCase.withInteraction(Interaction(action, Some(ActionObject(NameGroup(determinant, name)))))
      )
  }

  implicit def useCaseHelperToDomain(useCaseHelper: UseCaseInDomainHelper): Domain =
    useCaseHelper.toDomain

}
