package com.ezoky.ezmodel.actor

import akka.actor._
import akka.event.LoggingAdapter
import com.ezoky.ezmodel.actor.Clerk.Command

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
    val officeName = classTag.runtimeClass.getSimpleName
    getOrCreateChild(Props(Office[T]()), officeName)
  }

  private def getChild(name: String)(implicit context: ActorContext): Option[ActorRef] = context.child(name)

  private def createChild(props: Props, name: String)(implicit context: ActorContext, log: LoggingAdapter): ActorRef = {
    val actorRef: ActorRef = context.actorOf(props, name)
    log.info(s"Actor created $actorRef")
    actorRef
  }

  private def getOrCreateChild(props: Props, name: String)(implicit context: ActorContext, log: LoggingAdapter): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

case class Office[T](implicit classTag: ClassTag[T]) extends Actor with ActorLogging {

  import com.ezoky.ezmodel.actor.Office._

  override def receive: Receive = {
    case command: Command[_] =>
      val actorId = command.targetActorId
      val actorName = command.targetActorName
      implicit val log = this.log
      val actorRef = getOrCreateChild(Props(classTag.runtimeClass, actorId), actorName)
      actorRef forward command
    case m => throw new MessageUnhandledByRepository(m)
  }

  class MessageUnhandledByRepository(message: Any) extends RuntimeException(s"Message $message is not handled by Office[${classTag.getClass.getSimpleName}]")

}
