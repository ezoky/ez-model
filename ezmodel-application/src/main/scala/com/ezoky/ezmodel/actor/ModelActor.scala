package com.ezoky.ezmodel.actor

import akka.actor.{Actor, Props}

/**
 * @author gweinbach
 */
class ModelActor extends Actor {

  val domains = context.actorOf(Props(Office[DomainClerk]()), "Domains")
  //val stateMachines = context.actorOf(Props(Office[StateMachineActor]),"StateMachines")

  override def receive: Receive = ???
}
