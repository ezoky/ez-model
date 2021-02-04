package com.ezoky.ezmodel.actor

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.event.LoggingReceive
import Clerk._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities._
import com.ezoky.ezmodel.actor.Clerk.{Command, Event}
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities.{Entity, Multiplicity, single}
import com.typesafe.config.ConfigFactory

/**
 * @author gweinbach
 */
object EntityClerk {
  type EntityCommand = Command[Name]
  type EntityEvent = Event[Entity]

  case class CreateEntity(name: Name) extends EntityCommand(name)

  case class EntityCreated(entity: Entity)(implicit override val replyTo: ActorRef) extends EntityEvent(entity)(replyTo)

  case class AddAttribute(entityName: Name, name: Name, multiplicity: Multiplicity = single, mandatory: Boolean = false) extends EntityCommand(entityName)

  case class AttributeAdded(entity: Entity)(implicit override val replyTo: ActorRef) extends EntityEvent(entity)(replyTo)

  def entityClerk(entityId: String)(implicit factory: ActorRefFactory) = factory.actorOf(Props(new EntityClerk(Name(entityId))), idToString(entityId))

}

trait EntityFactory extends Factory[Entity, Name] {
  this: Clerk[Entity, Name] =>

  import EntityClerk._

  override def createCommand = CreateEntity(_)

  override def createAction = Entity(_)

  override def createdEvent = EntityCreated(_)(_)
}

class EntityClerk(name: Name) extends Clerk[Entity, Name] with EntityFactory {

  import EntityClerk._

  override def businessId = name

  override def receiveCommand = LoggingReceive {

    case AddAttribute(_, name, multiplicity, mandatory) =>
      val entity = state
      val nextEntity = entity.attribute(name, multiplicity, mandatory)
      persist(AttributeAdded(nextEntity)(sender()))(updateState)

  } orElse super.receiveCommand

  override def printState = {
    println(s"Actor: $self")
    if (isInitialised) {
      println(state)
      println(s"- attributes (${state.attributes.size}) =")
      state.attributes.foreach(a => println("  . " + a))
    }
  }
}

object EntityExample extends App {

  import EntityClerk._

  val system = ActorSystem("example", ConfigFactory.parseString( """
    akka {
      loglevel = "DEBUG"
      log-config-on-start = on
      actor {
        debug {
          lifecycle = on
          receive = on
          unhandled = on
        }
      }
    }"""))
  val office = system.actorOf(Props(Office[EntityClerk]()), "Entities")

  implicit val ref: ActorRef = system.deadLetters
  //  office ! AddAttribute(Name("AnotherEntity"),Name("a multiple mandatory attribute"), multiple, true)
  office ! AddAttribute(Name("Another Entity"), Name("an attribute"))
  //  office ! AddAttribute(Name("AnEntity"),Name("a multiple mandatory attribute"), multiple, true)
  //  office ! AddAttribute(Name("YetAnotherEntity"),Name("a multiple mandatory attribute"), multiple, true)
  //  Thread.sleep(1000)
  office ! Print(Name("An Entity"))
  office ! Print(Name("Another Entity"))
  office ! Print(Name("Yet Another Entity"))

  import akka.pattern.gracefulStop

import scala.concurrent.duration._
  import scala.concurrent.{Await, Future}
  import scala.language.postfixOps

  Thread.sleep(1000)

  try {
    val stopped: Future[Boolean] = gracefulStop(office, 5 seconds)
    Await.result(stopped, 6 seconds)
    // the actor has been stopped
  } catch {
    // the actor wasn't stopped within 5 seconds
    case e: akka.pattern.AskTimeoutException => println("the actor wasn't stopped within 5 seconds")
  }

  system.terminate()
}