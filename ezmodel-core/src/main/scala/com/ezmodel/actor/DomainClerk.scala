package com.ezmodel.actor

import akka.actor.{ActorLogging, ActorRef, ActorRefFactory, Props}
import akka.event.LoggingReceive
import com.ezmodel.actor.Clerk._
import com.ezmodel.actor.Office._
import com.ezmodel.core.Atoms.Name
import com.ezmodel.core.Domains.Domain

/**
 * @author gweinbach
 */
object DomainClerk {

  import com.ezmodel.core.UseCases.{Actor, Goal}

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

  import com.ezmodel.actor.DomainClerk._

  override def createCommand = CreateDomain(_)

  override def createAction = Domain(_)

  override def createdEvent = DomainCreated(_)(_)
}

class DomainClerk(name: Name) extends Clerk[Domain, Name] with DomainFactory with ActorLogging {

  import com.ezmodel.actor.DomainClerk._

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
      val next = state.entity(entity)
      persist(EntityAdded(next)(replyTo))(updateState)
      context.unbecome()
      unstashAll()

    case _ => stash()
  }

  def waitForUseCaseCreation(replyTo: ActorRef) = LoggingReceive {

    case UseCaseClerk.UseCaseCreated(useCase) =>
      val next = state.useCase(useCase)
      persist(UseCaseAdded(next)(replyTo))(updateState)
      context.unbecome()
      unstashAll()

    case _ => stash()
  }
}