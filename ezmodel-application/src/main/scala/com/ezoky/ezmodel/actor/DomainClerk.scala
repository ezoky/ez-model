package com.ezoky.ezmodel.actor

import akka.actor.{ActorLogging, ActorRef, ActorRefFactory, Props}
import akka.event.LoggingReceive
import Clerk._
import Office._
import com.ezoky.ezmodel.actor.Clerk.{Command, Event}
import com.ezoky.ezmodel.core.Models._

/**
  * @author gweinbach
  */
object DomainClerk {


  type DomainCommand = Command[Name]
  type DomainEvent = Event[Domain]

  case class CreateDomain(name: Name) extends DomainCommand(name)

  case class DomainCreated(domain: Domain)(implicit override val replyTo: ActorRef) extends DomainEvent(domain)(replyTo)

  case class CreateEntity(domainName: Name, name: Name) extends DomainCommand(domainName)

  case class EntityAdded(domain: Domain)(implicit override val replyTo: ActorRef) extends DomainEvent(domain)(replyTo)

  case class CreateUseCase(domainName: Name, actor: Actor, goal: Goal) extends DomainCommand(domainName)

  case class UseCaseAdded(domain: Domain)(implicit override val replyTo: ActorRef) extends DomainEvent(domain)(replyTo)

  def domainClerk(domainId: String)(implicit factory: ActorRefFactory) = factory.actorOf(Props(new DomainClerk(Name(domainId))), idToString(domainId))

}

trait DomainFactory extends Factory[Domain, Name] {
  this: Clerk[Domain, Name] =>

  import DomainClerk._

  override def createCommand = CreateDomain(_)

  override def createAction: Name => Domain = n => Domain(n)

  override def createdEvent = DomainCreated(_)(_)
}

class DomainClerk(name: Name) extends Clerk[Domain, Name] with DomainFactory with ActorLogging {

  import DomainClerk._

  override def businessId = name

  val useCases = office[UseCaseClerk]
  val entities = office[EntityClerk]

  override def receiveCommand = LoggingReceive {

    case DomainClerk.CreateEntity(_, entityName) =>
      entities ! EntityClerk.CreateEntity(entityName)
      context.become(waitForEntityCreation(sender()))

    case DomainClerk.CreateUseCase(_, actor, goal) =>
      useCases ! UseCaseClerk.CreateUseCase(actor, goal)
      context.become(waitForUseCaseCreation(sender()))
  }

  def waitForEntityCreation(replyTo: ActorRef) = LoggingReceive {

    case EntityClerk.EntityCreated(entity) =>
      val next = state.withEntity(entity)
      persist(EntityAdded(next)(replyTo))(updateState)
      context.unbecome()
      unstashAll()

    case _ => stash()
  }

  def waitForUseCaseCreation(replyTo: ActorRef) = LoggingReceive {

    case UseCaseClerk.UseCaseCreated(useCase) =>
      val next = state.withUseCase(useCase)
      persist(UseCaseAdded(next)(replyTo))(updateState)
      context.unbecome()
      unstashAll()

    case _ => stash()
  }
}