/*
 * IndexedSeqLike.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2014 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
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
    tree.find1(isSizeGtPred(idx))._2
  }

  // final def size : Int = sizeMeasure( tree.measure )
  def size: Int

  final def drop     (n: Int): Repr = wrap(dropTree(n))
  final def dropRight(n: Int): Repr = wrap(takeTree(size - n))

  final def slice(from: Int, until: Int): Repr = take(until).drop(from)

  final def splitAt(idx: Int): (Repr, Repr) = {
    val (l, r) = tree.span(isSizeLteqPred(idx))
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
  protected def isSizeLteqPred(idx: Int): V => Boolean

  /**
   * For a given value `i`, returns a test function that when passed a measure,
   * compare's the measure's size component against `i` using `f(m) > i`
   */
  protected def isSizeGtPred(idx: Int): V => Boolean

  private def takeTree(idx: Int) = tree.takeWhile(isSizeLteqPred(idx))
  private def dropTree(idx: Int) = tree.dropWhile(isSizeLteqPred(idx))

  // private def splitTree1(  i: Int ) = tree.split1( indexPred( i ))
}