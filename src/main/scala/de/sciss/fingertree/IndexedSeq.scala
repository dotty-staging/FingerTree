/*
 * IndexedSeq.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2014 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v3+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.fingertree

object IndexedSeq {
  private implicit val measure = Measure.Indexed

  def empty[A]: IndexedSeq[A] = new Impl[A](FingerTree.empty[Int, A])

  def apply[A](elems: A*): IndexedSeq[A] = new Impl[A](FingerTree.apply[Int, A](elems: _*))

  private final class Impl[A](protected val tree: FingerTree[Int, A]) extends IndexedSeq[A] {
    protected def m: Measure[A, Int] = measure

    protected def wrap(tree: FingerTree[Int, A]): IndexedSeq[A] = new Impl(tree)

    protected def isSizeGtPred  (i: Int) = _ > i
    protected def isSizeLteqPred(i: Int) = _ <= i

    def size: Int = tree.measure

    override def toString = tree.iterator.mkString("Seq(", ", ", ")")
  }
}
sealed trait IndexedSeq[A] extends IndexedSeqLike[Int, A, IndexedSeq[A]]
