package codingame.scala.kit.ghostcell

import codingame.scala.kit.graph.{Edge, Iti, ShortestPath}

/**
  * Created by hwang on 26/02/2017.
  */

object GhostCellConstant {
  val MAX_TURN = 20
}

case class Fac(id: Int, owner: Int, cyborgs: Int, production: Int, again: Int) {
  def mine: Boolean = owner == 1

  def other: Boolean = owner == -1

  def inc: Fac = copy(cyborgs = cyborgs - 10, production = production + 1)
}

case class Entity(entityId: Int, entityType: String, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)

case class Troop(id: Int, owner: Int, from: Int, to: Int, cyborgs: Int, arrival: Int) {
}

case class Bomb(id: Int, owner: Int, from: Int, to: Int, explosion: Int, birth: Int = 0) {
}

case class GhostCellGameState(factories: Vector[Fac],
                              troops: Vector[Troop],
                              bombs: Vector[Bomb],
                              turn: Int = 0,
                              undirectedEdges: Vector[Edge]) {

  private val edges = undirectedEdges.flatMap(edge => Vector(edge, Edge(edge.to, edge.from, edge.distance)))
  private val directDistances = edges.map(e => (e.from, e.to) -> e.distance).toMap
  private val itineraries = ShortestPath.shortestItinearies(factories.size, edges)
  val myFacs: Vector[Fac] = factories.filter(_.mine)
  val otherFacs: Vector[Fac] = factories.filter(_.other)

  def dist(from: Int, to: Int): Int = itineraries(from)(to).distance

  def transferFac(from : Int, to : Int) : Int = itineraries(from)(to).path.tail.head

  def directDist(from: Int, to: Int): Int = if (from == to) 0 else directDistances((from, to))

  def center: Fac = factories(0)

  def fac(id: Int): Fac = factories(id)

}

trait GhostCellAction {
  def command(): String
}

case object WaitAction extends GhostCellAction {
  override def command(): String = "WAIT"
}

sealed case class MoveAction(from: Int, to: Int, cyborgs: Int) extends GhostCellAction {
  override def command(): String = s"MOVE $from $to $cyborgs"
}

sealed case class IncreaseAction(factoryId: Int) extends GhostCellAction {
  override def command(): String = s"INC $factoryId"
}

sealed case class BombAction(from: Int, to: Int) extends GhostCellAction {
  override def command(): String = s"BOMB $from $to"
}

sealed case class MessageAction(msg: String) extends GhostCellAction {
  override def command(): String = s"MSG $msg"
}
