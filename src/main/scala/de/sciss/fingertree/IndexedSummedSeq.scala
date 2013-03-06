/*
 * IndexedSummedSeq.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2013 Hanns Holger Rutz. All rights reserved.
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 2, june 1991 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License (gpl.txt) along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.fingertree

object IndexedSummedSeq {
  val emptyIntLong: IndexedSummedSeq[Int, Long] = {
    implicit val m = Measure.IndexedSummedIntLong
    new Impl[Int, Long](FingerTree.empty)
  }

  def applyIntLong(elems: Int*): IndexedSummedSeq[Int, Long] = {
    implicit val m = Measure.IndexedSummedIntLong
    new Impl[Int, Long](FingerTree(elems: _*))
  }

  def empty[Elem, Sum](implicit m: Measure[Elem, Sum]): IndexedSummedSeq[Elem, Sum] = {
    implicit val m2 = Measure.Indexed.zip(m)
    new Impl[Elem, Sum](FingerTree.empty)
  }

  def apply[Elem, Sum](elems: Elem*)(implicit m: Measure[Elem, Sum]): IndexedSummedSeq[Elem, Sum] = {
    implicit val m2 = Measure.Indexed.zip(m)
    new Impl[Elem, Sum](FingerTree(elems: _*))
  }

  private final class Impl[Elem, Sum](protected val tree: FingerTree[(Int, Sum), Elem])
                                     (implicit protected val m: Measure[Elem, (Int, Sum)])
    extends IndexedSummedSeq[Elem, Sum] {

    protected def wrap(tree: FingerTree[(Int, Sum), Elem]): IndexedSummedSeq[Elem, Sum] = new Impl(tree)

    protected def isSizeGtPred  (i: Int) = _._1 > i
    protected def isSizeLteqPred(i: Int) = _._1 <= i

    def size: Int = tree.measure._1
    def sum: Sum  = tree.measure._2

    override def toString = tree.iterator.mkString("Seq<sum=" + sum + ">(", ", ", ")")
  }
}
sealed trait IndexedSummedSeq[Elem, Sum] extends IndexedSeqLike[(Int, Sum), Elem, IndexedSummedSeq[Elem, Sum]] {
  def sum: Sum
}