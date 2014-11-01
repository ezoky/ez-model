package com.ezoky.ezmodel.actor

import akka.actor.{ActorRefFactory, ActorRef, ActorSystem, Props}
import com.ezoky.ezmodel.actor.Clerk.{Command, Event, Print}
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}

/**
 * @author gweinbach
 */
object UseCaseClerk {
  type UseCaseCommand = Command[(Actor, Goal)]
  type UseCaseEvent = Event[UseCase]

  case class CreateUseCase(actor: Actor, goal: Goal) extends UseCaseCommand((actor, goal))

  case class UseCaseCreated(useCase: UseCase)(implicit override val replyTo: ActorRef) extends UseCaseEvent(useCase)(replyTo)

  def useCaseClerk(actor:Actor,goal:Goal)(implicit factory:ActorRefFactory) = factory.actorOf(Props(new UseCaseClerk(actor,goal)), "as a $actor I want to $goal")

}


trait UseCaseFactory extends Factory[UseCase, (Actor, Goal)] {
  this: Clerk[UseCase, (Actor, Goal)] =>

  import com.ezoky.ezmodel.actor.UseCaseClerk._

  override def createCommand = (id: (Actor, Goal)) => CreateUseCase(id._1, id._2)
  override def createAction = (id: (Actor, Goal)) => UseCase(id._1, id._2)
  override def createdEvent = UseCaseCreated(_)(_)
}


class UseCaseClerk(actor: Actor, goal: Goal) extends Clerk[UseCase, (Actor, Goal)] with UseCaseFactory {

  override def businessId = (actor, goal) //s"as a $actor I want to $goal"

  override def printState() = {
    println(s"Actor: $self")
    if (isInitialised) {
      println(state)
    }
  }

  override def receiveCommand = ???
}

object UseCaseExample extends App {

  val system = ActorSystem("example")
  val repository = system.actorOf(Props(Office[UseCaseClerk]), "UseCases")

  implicit val ref = system.deadLetters
  //office ! AddAttribute(Name("AnUseCase"),Name("an attribute"))
  //office ! AddAttribute(Name("AnUseCase"),Name("a multiple mandatory attribute"), multiple, true)
  repository ! Print(Name("AnEntity"))

  Thread.sleep(1000)
  system.shutdown()
}
