package nv.common.ddd.domain

import akka.actor.{ ReceiveTimeout, ActorLogging }
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.{ RecoveryCompleted, SnapshotOffer, PersistentActor }
import nv.common.ddd.domain.AggregateRoot.Commands.{ SaveSnapshot, GetState, Stop }
import nv.common.ddd.domain.AggregateRoot.Exceptions.{ StateNotInitialized, UnHandledCommandReceived }

import scala.reflect.ClassTag

import scala.concurrent.duration._

object AggregateRoot {

  object Commands {

    case object GetState

    case object Stop

    case object SaveSnapshot

  }

  object Exceptions {

    case object StateNotInitialized extends Exception

    case object AlreadyInitialized extends Exception

    case class UnHandledCommandReceived(command: Any) extends Exception

  }

}

trait AggregateRoot[S <: AggregateState[S, E], E <: DomainEvent] extends PersistentActor with ActorLogging {
  implicit def domainEventClassTag: ClassTag[E]

  implicit def aggregateStateClassTag: ClassTag[S]

  val domainEventTag = domainEventClassTag

  def initialState: S

  type ReceiveEvent = PartialFunction[E, Unit]

  val receiveTimeout = 120 seconds

  val snapShotPeriod = 50

  context.setReceiveTimeout(receiveTimeout)

  private var stateOpt: Option[S] = None

  def state: S = stateOpt.getOrElse(initialState)

  def initialized = stateOpt.isDefined

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  private var numOfEvents = 0

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: S) ⇒
      stateOpt = Some(snapshot)
    case event: E ⇒
      update(event)
    case RecoveryCompleted ⇒
      checkSnap()
  }

  override def receiveCommand: Receive = handleBasicCommand orElse handleCommandMessage orElse unHandledCommand

  private def handleBasicCommand: Receive = {
    case ReceiveTimeout ⇒
      context.parent ! Passivate(stopMessage = Stop)
    case Stop ⇒
      context.stop(self)
    case GetState ⇒
      sender() ! state
    case SaveSnapshot ⇒
      saveSnapshot(state)
  }

  private def unHandledCommand: Receive = {
    case c ⇒
      sender() ! akka.actor.Status.Failure(UnHandledCommandReceived(c))
  }

  private def handleCommandMessage: Receive = {
    case cm: Command[_] ⇒
      handleCommand(cm)
  }

  def handleCommand: Receive

  /**
    * 状態が更新された後に実行される。
    * 外部に対する振る舞い（Publish）などを実行する。
    * Recovery時は実行されない。
    * @return
    */
  def afterEvent: ReceiveEvent = {
    case _ ⇒
  }

  private def checkSnap() = {
    if (numOfEvents >= snapShotPeriod) {
      self ! SaveSnapshot
      numOfEvents = 0
    }
  }

  private def update(event: E): Unit = {
    stateOpt match {
      case Some(_) ⇒
      case None ⇒
        stateOpt = Some(initialState)
    }
    stateOpt = Some(state.handle(event))

    numOfEvents += 1
  }

  def raise(event: E): Unit = {
    persist(event) {
      event ⇒
        update(event)
        afterEvent(event)
        sender() ! event
        checkSnap()
    }
  }
}

trait AggregateState[S <: AggregateState[S, E], E <: DomainEvent] {
  type HandleState = PartialFunction[E, S]

  def handle: HandleState

}