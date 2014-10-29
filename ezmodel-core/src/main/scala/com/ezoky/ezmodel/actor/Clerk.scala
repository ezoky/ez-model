package com.ezoky.ezmodel.actor

import akka.actor.{ActorRef, ActorLogging, Stash}
import akka.event.LoggingReceive
import akka.persistence.{PersistentActor, RecoveryCompleted}

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
object Clerk {

  import java.net.URLEncoder

  def idToString(businessId:Any) =  URLEncoder.encode(businessId.toString,"UTF-8")

  abstract class Command[I](val targetActorId:I) extends Serializable {
    def targetActorName = idToString(targetActorId)
  }
  abstract class Event[S](val state:S) extends Serializable
  case class Print[I](override val targetActorId:I) extends Command[I](targetActorId)
  case class Reset[I](override val targetActorId:I) extends Command[I](targetActorId)

  case object Snap

  final val EVENT_PERSISTED_LOG_MESSAGE = "Event persisted: "
}

class ClerkNotInitializedException extends RuntimeException


trait Factory[S,I] {

  import com.ezoky.ezmodel.actor.Clerk._

  def createCommand: (I => Command[I])
  def createAction: (I => S)
  def createdEvent: (S => Event[S])
}

/**
 * Command handler
 *
 * @param classTag classTag of type S (actor state)
 * @tparam S Type of elements used as actor state
 * @tparam I Type of unique (in the business context) business identifier of both actor and state
 */
abstract class Clerk[S,I](implicit classTag: ClassTag[S]) extends PersistentActor with ActorLogging with Stash with Factory[S,I] {

  import akka.persistence.{RecoveryFailure, SnapshotOffer}
  import com.ezoky.ezmodel.actor.Clerk._

  def businessId:I

  override def persistenceId = s"${classTag.runtimeClass.getSimpleName}/${idToString(businessId)}"

  private var stateOption: Option[S] = None

  protected def state = if (isInitialised) stateOption.get else throw new ClerkNotInitializedException

  def isInitialised = stateOption.isDefined

  def triggerInitialisation() = {
    self ! createCommand(businessId)
  }

  def initActor: Receive  = {

    LoggingReceive {

      case cmd: Command[_] =>

        if (cmd == createCommand(businessId)) {
          if (!isInitialised) {
            val entity = createAction(businessId)
            //val commandSender = cmd.ref
            persist(createdEvent(entity))(initState)
            unstashAll()
            context.unbecome()
          }
          else {
            log.warning(s"Received $createCommand command but actor is already initialized")
          }
        }
        else stash()

      // we won't accept any command until Actor is initialized
      case _ => stash()
    }
  }

  def updateState(event: Event[S]) = {
    log.info(EVENT_PERSISTED_LOG_MESSAGE + "{}", event)
    stateOption = Some(event.state)
    log.info(s"Sending back event $event to sender of command (${sender().path})")
    sender ! event
    context.system.eventStream.publish(event)
  }

  def initState(event: Event[S]) = {
    stateOption = Some(event.state)
    log.info(s"Sending back event $event to parent (${context.parent.path})")
    context.parent ! event
    context.system.eventStream.publish(event)
  }

  def printState = {
    if (isInitialised) println(state.toString)
    else println("<no state>")
  }

  override def receiveCommand: Receive = LoggingReceive {
    case Reset =>
      stateOption = None
      triggerInitialisation()

    case Snap =>
      saveSnapshot(state)

    case Print(_) =>
      printState

    case cmd =>
      log.info(s"Unhandled command received $cmd by ${self.path}")
  }

  override def receiveRecover: Receive = {

    case RecoveryCompleted =>
      log.info("Recovery completed")
      if (!isInitialised) {
        log.info("Recovery completed but actor is still not initialized. Let's do it.")
        triggerInitialisation()
        context.become(initActor)
      }

    case evt: Event[S] =>
      updateState(evt)

    case SnapshotOffer(_, snapshot: S) =>
      stateOption = Some(snapshot)

    case RecoveryFailure(cause) =>
      log.info(s"Trying to recover $persistenceId from failure: $cause")
      deleteMessages(lastSequenceNr)
      self ! Reset
      log.info(s"Recovery started for $persistenceId. State reset has been triggered.")
  }
}