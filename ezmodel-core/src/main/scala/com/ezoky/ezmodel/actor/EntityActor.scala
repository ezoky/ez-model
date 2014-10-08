package com.ezoky.ezmodel.actor

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.event.LoggingReceive
import com.ezoky.ezmodel.actor.EntityActor.{CreateEntity, AddAttribute}
import com.ezoky.ezmodel.actor.PersistentActor._
import com.ezoky.ezmodel.core.Atoms.Name
import com.ezoky.ezmodel.core.Entities._
import com.typesafe.config.ConfigFactory

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
object EntityActor {
  type EntityCommand = Command[Name]
  type EntityEvent = Event[Entity]

  case class CreateEntity(name: Name) extends EntityCommand(name)

  case class EntityCreated(entity: Entity) extends EntityEvent(entity)

  case class AddAttribute(entityName:Name, name: Name, multiplicity: Multiplicity = single, mandatory: Boolean = false) extends EntityCommand(entityName)

  case class AddedAttribute(entity: Entity) extends EntityEvent(entity)

}

class EntityActor(name: Name) extends PersistentActor[Entity, Name] {

  import com.ezoky.ezmodel.actor.EntityActor._

  override def businessId = name

  object EntityFactory {
    def create(name: Name) = Entity(name)
  }

  override def initState = {
    self ! CreateEntity(name)
  }

  override def initActor = LoggingReceive {

    case CreateEntity(name) =>
      if (!isInitialized) {
        val entity = EntityFactory.create(name)
        persist(EntityCreated(entity))(updateState)
        unstashAll()
        context.become(receiveCommand)
      }
      else {
        log.warning(s"Received CreateEntity($name) command but actor is already initialized")
      }

    // we won't accept any command until Actor is initialized
    case _ => stash()
  }

  override def receiveCommand = LoggingReceive ({

    case AddAttribute(_, name, multiplicity, mandatory) =>
      if (isInitialized) {
        val entity = state
        val nextEntity = entity.attribute(name, multiplicity, mandatory)
        persist(AddedAttribute(nextEntity))(updateState)
      }
      else {
        log.warning(s"Received AddAttribute($name) command but actor is not initialized")
      }

  }: Receive) orElse super.receiveCommand

  override def printState = {
    println(s"Actor: $self")
    if (isInitialized) {
      println(state)
      println(s"- attributes (${state.attributes.size}) =")
      state.attributes.foreach(a => println("  . " + a))
    }
  }
}

object EntityExample extends App {

  val system = ActorSystem("example",ConfigFactory.parseString("akka { " +
    "loglevel = \"DEBUG\"\n" +
    "log-config-on-start = on\n" +
    " actor {" +
    "   debug {" +
    "     lifecycle = on\n receive = on\n unhandled = on" +
    "   }" +
    " }" +
    "}"))
  val office = system.actorOf(Props(Office[EntityActor]), "Entities")

//  office ! AddAttribute(Name("AnotherEntity"),Name("a multiple mandatory attribute"), multiple, true)
  office ! AddAttribute(Name("AnotherEntity"),Name("an attribute"))
//  office ! AddAttribute(Name("AnEntity"),Name("a multiple mandatory attribute"), multiple, true)
//  office ! AddAttribute(Name("YetAnotherEntity"),Name("a multiple mandatory attribute"), multiple, true)
//  Thread.sleep(1000)
  office ! Print(Name("AnEntity"))
  office ! Print(Name("AnotherEntity"))
  office ! Print(Name("YetAnotherEntity"))

  import akka.pattern.gracefulStop
  import scala.concurrent.{Future, Await}
  import scala.concurrent.duration._
  import scala.language.postfixOps

  Thread.sleep(1000)

  try {
    val stopped: Future[Boolean] = gracefulStop(office, 5 seconds)
    Await.result(stopped, 6 seconds)
    // the actor has been stopped
  } catch {
    // the actor wasn't stopped within 5 seconds
    case e: akka.pattern.AskTimeoutException ⇒ println("the actor wasn't stopped within 5 seconds")
  }

  system.shutdown()
}