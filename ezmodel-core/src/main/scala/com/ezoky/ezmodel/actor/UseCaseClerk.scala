package com.ezoky.ezmodel.actor

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.event.LoggingReceive
import Clerk._
import UseCaseClerk.{AddPostCondition, AddPreCondition, ConstrainedUseCase}
import com.ezoky.ezmodel.core.Entities.EntityState
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}
import com.ezoky.ezmodel.actor.Clerk.{Command, Event}
import com.ezoky.ezmodel.core.Entities.EntityState
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}

/**
 * @author gweinbach
 */
object UseCaseClerk {
  type UseCaseCommand = Command[(Actor, Goal)]
  type UseCaseEvent = Event[UseCase]

  case class CreateUseCase(actor: Actor, goal: Goal) extends UseCaseCommand((actor, goal))

  case class UseCaseCreated(useCase: UseCase)(implicit override val replyTo: ActorRef) extends UseCaseEvent(useCase)(replyTo)

  case class AddPreCondition(actor: Actor, goal: Goal, entityState: EntityState) extends UseCaseCommand((actor, goal))

  case class AddPostCondition(actor: Actor, goal: Goal, entityState: EntityState) extends UseCaseCommand((actor, goal))

  case class ConstrainedUseCase(useCase: UseCase)(implicit override val replyTo: ActorRef) extends UseCaseEvent(useCase)(replyTo)

  def useCaseClerk(actor: Actor, goal: Goal)(implicit factory: ActorRefFactory) = factory.actorOf(Props(new UseCaseClerk(actor, goal)), idToString(s"as a $actor I want to $goal"))

}


trait UseCaseFactory extends Factory[UseCase, (Actor, Goal)] {
  this: Clerk[UseCase, (Actor, Goal)] =>

  import UseCaseClerk._

  override def createCommand = (id: (Actor, Goal)) => CreateUseCase(id._1, id._2)

  override def createAction = (id: (Actor, Goal)) => UseCase(id._1, id._2)

  override def createdEvent = UseCaseCreated(_)(_)
}


class UseCaseClerk(id: (Actor, Goal)) extends Clerk[UseCase, (Actor, Goal)] with UseCaseFactory {

  override def businessId = id //s"as a $actor I want to $goal"

  override def printState() = {
    println(s"Actor: $self")
    if (isInitialised) {
      println(state)
    }
  }

  override def receiveCommand = LoggingReceive {

    case AddPreCondition(_, _, entityState) =>
      val useCase = state
      val nextUseCase = useCase.preCondition(entityState)
      persist(ConstrainedUseCase(nextUseCase)(sender()))(updateState)

    case AddPostCondition(_, _, entityState) =>
      val useCase = state
      val nextUseCase = useCase.postCondition(entityState)
      persist(ConstrainedUseCase(nextUseCase)(sender()))(updateState)

  } orElse super.receiveCommand
}

object UseCaseExample extends App {

  val system = ActorSystem("example")
  val repository = system.actorOf(Props(Office[UseCaseClerk]), "UseCases")

  implicit val ref = system.deadLetters
  //office ! AddAttribute(Name("AnUseCase"),Name("an attribute"))
  //office ! AddAttribute(Name("AnUseCase"),Name("a multiple mandatory attribute"), multiple, true)
  repository ! Print((Actor("an actor"), Goal("to do something")))

  Thread.sleep(1000)
  system.terminate()
}
