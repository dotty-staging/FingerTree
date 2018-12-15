/*
 * FingerTreeLike.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2018 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.fingertree

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

trait FingerTreeLike[V, A, Repr <: FingerTreeLike[V, A, Repr]] {
  protected implicit def m: Measure[A, V]

  final def iterator: Iterator[A] = tree.iterator

  final def isEmpty : Boolean = tree.isEmpty
  final def nonEmpty: Boolean = !isEmpty

  final def head: A               = tree.head
  final def headOption: Option[A] = tree.headOption

  final def last: A               = tree.last
  final def lastOption: Option[A] = tree.lastOption

  final def init: Repr = wrap(tree.init)
  final def tail: Repr = wrap(tree.tail)

  // final def foreach[ U ]( f: A => U ) { tree.foreach( f )}

  final def toList: List[A] = tree.toList
  final def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A]]): Col[A] = tree.to[Col]

  // def toStream : Stream[ A ] = tree.toStream

  protected def tree: FingerTree[V, A]

  protected def wrap(tree: FingerTree[V, A]): Repr
}
