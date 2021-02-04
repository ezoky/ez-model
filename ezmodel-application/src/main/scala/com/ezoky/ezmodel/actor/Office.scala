package com.ezoky.ezmodel.actor

import akka.actor._
import akka.event.{LoggingAdapter, LoggingReceive}
import Clerk.{Command, Event}
import com.ezoky.ezmodel.actor.Clerk.{Command, Event}

import scala.reflect.ClassTag

/**
 * Lookup for a matching Actor (or creates it if it does not exit) then forwards every Command message to it
 *
 * @author gweinbach
 */
object Office {
  /** Office is identified by the type of Actors it is dealing with and the fact that context is implicit entails
    * uniqueness of an Office dealing with a particular type of Actors in a particular context
    */
  def office[T](implicit classTag: ClassTag[T], context: ActorContext, log: LoggingAdapter) = {
    getOrCreateChild(props[T], classTag.runtimeClass.getSimpleName)
  }

  def props[T](implicit classTag: ClassTag[T]) = {
    val officeName = classTag.runtimeClass.getSimpleName
    Props(Office[T]())
  }

  private def getChild(name: String)(implicit context: ActorContext): Option[ActorRef] = context.child(name)

  private def createChild(props: Props, name: String)(implicit context: ActorRefFactory, log: LoggingAdapter): ActorRef = {
    val actorRef: ActorRef = context.actorOf(props, name)
    log.info(s"Actor created $actorRef")
    actorRef
  }

  private def getOrCreateChild(props: Props, name: String)(implicit context: ActorContext, log: LoggingAdapter): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

case class Office[T]()(implicit classTag: ClassTag[T]) extends Actor with ActorLogging {

  import Office._

  implicit val logger: LoggingAdapter = this.log

  override def receive: Receive = LoggingReceive {
    case command: Command[_] =>
      val actorId = command.targetActorId
      val actorName = command.targetActorName
      val actorRef = getOrCreateChild(Props(classTag.runtimeClass, actorId), actorName)
      actorRef forward command

    // as forwarding was done for Commands, this should happen only during creation process (which means Event is the result of Actor
    // creation an should be forwarded to parent of the Office)
    case event: Event[_] =>
      log.debug(s"Forwarding Event $event to original Command sender ${context.parent}")
      context.parent forward event
    case m => throw new MessageUnhandledByOffice(m)
  }

  class MessageUnhandledByOffice(message: Any) extends RuntimeException(s"Message $message is not handled by Office[${classTag.getClass.getSimpleName}]")

}
