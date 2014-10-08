package com.ezoky.ezmodel.actor

import akka.actor.{Stash, ActorLogging}
import akka.event.LoggingReceive

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
object PersistentActor {

  import java.net.URLEncoder

  abstract class Command[I](val targetActorId:I) extends Serializable {
    def targetActorName = URLEncoder.encode(targetActorId.toString,"UTF-8")
  }
  abstract class Event[S](val state:S) extends Serializable
  case class Print[I](override val targetActorId:I) extends Command[I](targetActorId)
  case class Reset[I](override val targetActorId:I) extends Command[I](targetActorId)

  case object Snap
}

class ActorNotInitializedException extends RuntimeException

abstract class PersistentActor[S,I](implicit classTag: ClassTag[S]) extends akka.persistence.PersistentActor with ActorLogging with Stash {

  initState()

  import akka.persistence.{RecoveryFailure, SnapshotOffer}

  import com.ezoky.ezmodel.actor.PersistentActor._

  def businessId:I

  override def persistenceId = s"${classTag.runtimeClass.getSimpleName}/${businessId.toString}"

  private var stateOption: Option[S] = None

  protected def state = if (isInitialized) stateOption.get else throw new ActorNotInitializedException

  def isInitialized = stateOption.isDefined

  def initState(): Unit

  def updateState(event: Event[S]) = {
    stateOption = Some(event.state)
    log.info(s"Sending back event $event to sender of command (${sender.path}")
    sender ! event
    context.system.eventStream.publish(event)
  }

  def printState = {
    if (isInitialized) println(state.toString)
    else println("<no state>")
  }

  override def receive = initActor

  def initActor: Receive


  override def receiveCommand: Receive = LoggingReceive {
    case Reset => {
      stateOption = None
      initState()
      self ! Snap
    }
    case Snap => saveSnapshot(state)
    case Print(_) => printState
    case cmd => println(s"Unhandled command received $cmd by ${self.path}")
  }

  override def receiveRecover: Receive = {
    case evt: Event[S] => updateState(evt)
    case SnapshotOffer(_, snapshot: S) => stateOption = Some(snapshot)
    case RecoveryFailure(cause) => {
      log.info(s"Trying to recover $persistenceId from failure: $cause");
      deleteMessages(lastSequenceNr)
      self ! Reset
      log.info(s"Recovery complete for $persistenceId. State has been reset");
    }
  }
}