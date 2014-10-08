package com.ezoky.ezmodel.actor

import akka.actor.Props
import com.ezoky.ezmodel.actor.PersistentActor.{Command, Event}
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Domains.Domain
import com.ezoky.ezmodel.core.Entities.Entity
import com.ezoky.ezmodel.core.UseCases.UseCase

/**
 * @author gweinbach
 */
object DomainActor {

  import com.ezoky.ezmodel.core.UseCases.{Actor,Goal}

  type DomainCommand = Command[Name]
  type DomainEvent = Event[Domain]

  case class CreateDomain(name: Name) extends DomainCommand(name)
  case class DomainCreated(domain:Domain) extends DomainEvent(domain)

  case class CreateEntity(domainName:Name,name: Name) extends DomainCommand(domainName)
  case class EntityAdded(domain: Domain) extends DomainEvent(domain)

  case class CreateUseCase(domainName:Name,actor: Actor, goal: Goal) extends DomainCommand(domainName)
  case class UseCaseAdded(domain: Domain) extends DomainEvent(domain)

}

class DomainActor(name:Name) extends PersistentActor[Domain,Name] {

  import com.ezoky.ezmodel.actor.EntityActor._
  import com.ezoky.ezmodel.actor.DomainActor._

  override def businessId = name

  object DomainFactory {
    def create(name: Name) = Domain(name)
  }

  val useCases = context.actorOf(Props(Office[UseCaseActor]),"UseCases")
  val entities = context.actorOf(Props(Office[EntityActor]),"Entities")

  override def initState() = {
    if (!isInitialized) {
      self ! CreateDomain(name)
    }
  }

  override def receiveCommand = {
    case CreateDomain(name) if (!isInitialized) =>
      val domain = DomainFactory.create(name)
      persist(DomainCreated(domain))(updateState)

    case DomainActor.CreateEntity(_, name) if isInitialized =>
      entities ! EntityActor.CreateEntity(name)

    case EntityActor.EntityCreated(entity) if isInitialized =>
      val next = state.entity(entity)
      persist(EntityAdded(next))(updateState)

    case DomainActor.CreateUseCase(_,actor, goal) if isInitialized =>
      useCases ! UseCaseActor.CreateUseCase(actor,goal)

    case UseCaseActor.UseCaseCreated(useCase) if isInitialized =>
      val next = state.useCase(useCase)
      persist(UseCaseAdded(next))(updateState)
  }

  override def initActor: Receive = ???
}