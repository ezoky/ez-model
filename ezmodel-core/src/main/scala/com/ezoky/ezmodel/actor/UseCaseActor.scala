package com.ezoky.ezmodel.actor

import akka.actor.{Props, ActorSystem}
import com.ezoky.ezmodel.actor.Clerk.{Print, Command, Event}
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}

/**
 * @author gweinbach
 */
object UseCaseActor {
  type UseCaseCommand = Command[(Actor,Goal)]
  type UseCaseEvent = Event[UseCase]

  case class CreateUseCase(actor:Actor,goal:Goal) extends UseCaseCommand((actor,goal))

  case class UseCaseCreated(useCase: UseCase) extends UseCaseEvent(useCase)

}

class UseCaseActor(actor:Actor,goal:Goal) extends Clerk[UseCase, Name] {

  import com.ezoky.ezmodel.actor.UseCaseActor._

  object UseCaseFactory {
    def create(actor: Actor, goal: Goal) = UseCase(actor, goal)
  }

  override def businessId = s"as a $actor I want to $goal"

  override def initState() = {
    if (!isInitialized) {
      self ! CreateUseCase(actor, goal)
    }
  }

  override def printState() = {
    println(s"Actor: $self")
    if (isInitialized) {
      println(state)
    }
  }

  override def receiveCommand = ({
    case CreateUseCase(actor, goal) if !isInitialized =>
      val useCase = UseCaseFactory.create(actor, goal)
      persist(UseCaseCreated(useCase))(updateState)

  }: Receive) orElse super.receiveCommand


  override def initActor: Receive = ???
}

object UseCaseExample extends App {

val system = ActorSystem("example")
val repository = system.actorOf(Props(Office[UseCaseActor]), "UseCases")

//office ! AddAttribute(Name("AnEntity"),Name("an attribute"))
//office ! AddAttribute(Name("AnEntity"),Name("a multiple mandatory attribute"), multiple, true)
repository ! Print(Name("AnEntity"))

Thread.sleep(1000)
system.shutdown()
}
