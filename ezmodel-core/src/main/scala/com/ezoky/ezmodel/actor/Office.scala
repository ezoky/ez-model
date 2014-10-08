package com.ezoky.ezmodel.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.ezoky.ezmodel.actor.PersistentActor.Command

import scala.reflect.ClassTag

/**
 * @author gweinbach
 */
case class Office[T](implicit classTag: ClassTag[T]) extends Actor with ActorLogging {

  override def receive: Receive = {
    case command: Command[_] =>
      val actorId = command.targetActorId
      val actorName = command.targetActorName
      val actor = getOrCreateChild(Props(classTag.runtimeClass,actorId),actorName)
      actor forward command
    case m => throw new MessageUnhandledByRepository(m)
  }

  private def getChild(name: String): Option[ActorRef] = context.child(name)

  private def createChild(props: Props, name: String): ActorRef = {
    val actor: ActorRef = context.actorOf(props, name)
    log.info(s"PersistentActor created $actor")
    actor
  }

  private def getOrCreateChild(props: Props, name: String): ActorRef = getChild(name).getOrElse(createChild(props, name))

  class MessageUnhandledByRepository(message: Any) extends RuntimeException(s"Message $message is not handled by Repository")

}
