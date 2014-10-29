package com.ezoky.ezmodel.actor

import akka.actor._
import akka.testkit.{EventFilter, ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import com.ezoky.ezmodel.actor.Clerk._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.util.Failure


abstract class ClerkTestKit[A](_system: ActorSystem)(implicit arClassTag: ClassTag[A])
  extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  //  with MockitoSugar
  //  with Matchers
  with BeforeAndAfterAll with BeforeAndAfter {

  //  val settings = EcommerceSettings(system)
  val domain: String

  //implicit val logger = new RainbowLogger(suiteName)
  //
  //  implicit def topLevelParent(implicit system: ActorSystem): CreationSupport = {
  //    new CreationSupport {
  //      override def getChild(name: String): Option[ActorRef] = None
  //      override def createChild(props: Props, name: String): ActorRef = {
  //        system.actorOf(props, name)
  //      }
  //    }
  //  }
  //
  //  implicit def defaultCaseIdResolution[A] = new AggregateIdResolution[A]

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
    system.awaitTermination()
  }

  def expectEventPublishedMatching[E](matcher: PartialFunction[Any, Boolean])(implicit t: ClassTag[E]) {
    val probe = TestProbe()
    system.eventStream.subscribe(probe.ref, t.runtimeClass)
    assert(probe.expectMsgPF[Boolean](10 seconds)(matcher), s"unexpected event")

  }

  def expectEventPublished[E](implicit t: ClassTag[E]) {
    val probe = TestProbe()
    system.eventStream.subscribe(probe.ref, t.runtimeClass)
    probe.expectMsgClass(10 seconds, t.runtimeClass)
  }

  def expectEventPersisted[E, I](aggregateId: I)(when: => Unit)(implicit t: ClassTag[E]) {
    expectLogMessageFromClerk[I](EVENT_PERSISTED_LOG_MESSAGE + t.runtimeClass.getSimpleName, when)(aggregateId)
  }

  def expectEventPersisted[E, I](event: E)(aggregateRootId: I)(when: => Unit) {
    expectLogMessageFromClerk[I](EVENT_PERSISTED_LOG_MESSAGE + event.toString, when)(aggregateRootId)
  }

  def expectLogMessageFromClerk[I](messageStart: String, when: => Unit)(aggregateId: I) {
    EventFilter.info(
      source = s"akka://Tests/user/$domain/${idToString(aggregateId)}",
      start = messageStart, occurrences = 1)
      .intercept {
      when
    }
  }

  def expectExceptionLogged[E <: Throwable](when: => Unit)(implicit t: ClassTag[E]) {
    EventFilter[E](occurrences = 1) intercept {
      when
    }
  }

  def expectLogMessageFromOffice(messageStart: String)(when: => Unit) {
    EventFilter.info(
      source = s"akka://Tests/user/$domain",
      start = messageStart, occurrences = 1)
      .intercept {
      when
    }
  }

  def expectFailure[E](awaitable: Future[Any])(implicit t: ClassTag[E]) {
    implicit val timeout = Timeout(5, SECONDS)
    val future = Await.ready(awaitable, timeout.duration)
    val futureValue = future.value.get
    futureValue match {
      case Failure(ex) if ex.getClass.equals(t.runtimeClass) => () //ok
      case x => fail(s"Unexpected result: $x")
    }
  }

  def expectReply[O](obj: O) {
    expectMsg(1 seconds, obj)
  }

  def ensureActorTerminated(actor: ActorRef) = {
    watch(actor)
    actor ! PoisonPill
    // wait until reservation office is terminated
    fishForMessage(1.seconds) {
      case Terminated(_) =>
        unwatch(actor)
        true
      case _ => false
    }
  }
}
