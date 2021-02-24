package com.ezoky.ezmodel.interaction.dsl

import com.ezoky.ezmodel.core.Models._


/**
  * @author gweinbach on 08/02/2021
  * @since 0.2.0
  */
trait DomainDSL {

  def inDomain(domainName: String): Domain =
    Domain(Name(domainName))

  implicit class DomainHelper(domain: Domain) {

    def asA(actorName: Name): FluentUseCase =
      FluentUseCase(domain, Actor(actorName))

    def asAn(actorName: Name): FluentUseCase = asA(actorName)

    case class FluentUseCase(domain: Domain,
                             actor: Actor) {

      def iWantTo(goal: Goal): Domain =
        domain.withUseCase(UseCase(actor, goal))

      // non transitive Action
      def iWantTo(action: Action): Domain =
        domain.withUseCase(UseCase(actor, Goal(action, None)))

      // transitive Action
      def iWantTo(action: Action,
                  actionObject: ActionObject): Domain =
        domain.withUseCase(UseCase(actor, Goal(action, Some(actionObject))))

      // transitive Action made simple
      def iWantTo(action: Action,
                  determinant: Determinant,
                  name: Name): Domain =
        domain.withUseCase(UseCase(actor, Goal(action, Some(ActionObject(NameGroup(determinant, name))))))
    }

  }
}
