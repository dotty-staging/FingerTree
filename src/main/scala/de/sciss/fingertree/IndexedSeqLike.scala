/*
 * IndexedSeqLike.scala
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

trait IndexedSeqLike[V, A, Repr <: IndexedSeqLike[V, A, Repr]] extends FingerTreeLike[V, A, Repr] {
  final def :+(x: A): Repr      = wrap(tree :+ x)
  final def +:(x: A): Repr      = wrap(x +: tree)
  final def ++(xs: Repr): Repr  = wrap(tree ++ xs.tree)

  final def apply(idx: Int): A = {
    if (idx < 0 || idx >= size) throw new IndexOutOfBoundsException(idx.toString)
    tree.find1(isSizeGtPred(idx))
  }

  // final def size : Int = sizeMeasure( tree.measure )
  def size: Int

  final def drop     (n: Int): Repr = wrap(dropTree(n))
  final def dropRight(n: Int): Repr = wrap(takeTree(size - n))

  final def slice(from: Int, until: Int): Repr = take(until).drop(from)

  final def splitAt(i: Int): (Repr, Repr) = {
    val (l, r) = tree.span(isSizeLteqPred(i))
    (wrap(l), wrap(r))
  }

  final def take     (n: Int): Repr = wrap(takeTree(n))
  final def takeRight(n: Int): Repr = wrap(dropTree(size - n))

  //   final def updated( index: Int, elem: A ) : Repr = {
  //      if( index < 0 || index >= size ) throw new IndexOutOfBoundsException( index.toString )
  //      val (l, _, r) = splitTree1( index )
  //      wrap( l.:+( elem ).<++>( r ))  // XXX most efficient?
  //   }

  /**
   * For a given value `i`, returns a test function that when passed a measure,
   * compare's the measure's size component against `i` using `f(m) <= i`
   */
  protected def isSizeLteqPred(i: Int): V => Boolean

  /**
   * For a given value `i`, returns a test function that when passed a measure,
   * compare's the measure's size component against `i` using `f(m) > i`
   */
  protected def isSizeGtPred(i: Int): V => Boolean

  private def takeTree(i: Int) = tree.takeWhile(isSizeLteqPred(i))
  private def dropTree(i: Int) = tree.dropWhile(isSizeLteqPred(i))

  // private def splitTree1(  i: Int ) = tree.split1( indexPred( i ))
}