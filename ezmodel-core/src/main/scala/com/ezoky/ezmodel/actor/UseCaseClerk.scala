package com.ezoky.ezmodel.actor

import akka.actor.{Props, ActorSystem}
import com.ezoky.ezmodel.actor.Clerk.{Print, Command, Event}
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.UseCases.{Actor, Goal, UseCase}

/**
 * @author gweinbach
 */
object UseCaseClerk {
  type UseCaseCommand = Command[(Actor,Goal)]
  type UseCaseEvent = Event[UseCase]

  case class CreateUseCase(actor:Actor,goal:Goal) extends UseCaseCommand((actor,goal))

  case class UseCaseCreated(useCase: UseCase) extends UseCaseEvent(useCase)
}


trait UseCaseFactory extends Factory[UseCase, (Actor,Goal)] {

  import com.ezoky.ezmodel.actor.UseCaseClerk._

  override def createCommand = (id:(Actor,Goal)) => CreateUseCase(id._1,id._2)

  override def createAction = (id: (Actor, Goal)) => UseCase(id._1, id._2)

  override def createdEvent = UseCaseCreated(_)
}


class UseCaseClerk(actor:Actor,goal:Goal) extends Clerk[UseCase, (Actor,Goal)] with UseCaseFactory  {

  import com.ezoky.ezmodel.actor.UseCaseClerk._

  override def businessId = (actor,goal) //s"as a $actor I want to $goal"

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

//office ! AddAttribute(Name("AnUseCase"),Name("an attribute"))
//office ! AddAttribute(Name("AnUseCase"),Name("a multiple mandatory attribute"), multiple, true)
repository ! Print(Name("AnEntity"))

Thread.sleep(1000)
system.shutdown()
}
