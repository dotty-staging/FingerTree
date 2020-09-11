/*
 * Measure.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2020 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.fingertree

object Measure {
  object Unit extends Measure[Any, Unit] {
    override def toString = "Unit"

    val zero: Unit = ()

    def apply(c: Any): Unit = ()

    def |+|(a: Unit, b: Unit): Unit = ()

    override def |+|(a: Unit, b: Unit, c: Unit): Unit = ()
  }

  object Indexed extends Measure[Any, Int] {
    override def toString = "Indexed"

    val zero: Int = 0

    def apply(c: Any): Int = 1

    def |+|(a: Int, b: Int): Int = a + b

    override def |+|(a: Int, b: Int, c: Int): Int = a + b + c
  }

  object SummedIntInt extends Measure[Int, Int] {
    override def toString = "SummedIntLong"

    val zero: Int = 0

    def apply(c: Int): Int = c

    def |+|(a: Int, b: Int): Int = a + b

    override def |+|(a: Int, b: Int, c: Int): Int = a + b + c
  }

  object SummedIntLong extends Measure[Int, Long] {
    override def toString = "SummedIntLong"

    val zero: Long = 0L

    def apply(c: Int): Long = c.toLong

    def |+|(a: Long, b: Long): Long = a + b

    override def |+|(a: Long, b: Long, c: Long): Long = a + b + c
  }

  object IndexedSummedIntLong extends Measure[Int, (Int, Long)] {
    override def toString = "IndexedSummedIntLong"

    val zero: (Int, Long) = (0, 0L)

    def apply(c: Int): (Int, Long) = (1, c.toLong)

    def |+|(a: (Int, Long), b: (Int, Long)): (Int, Long) = (a._1 + b._1, a._2 + b._2)

    override def |+|(a: (Int, Long), b: (Int, Long), c: (Int, Long)): (Int, Long) =
      (a._1 + b._1 + c._1, a._2 + b._2 + c._2)
  }

  private final class Zip[C, M, N](m1: Measure[C, M], m2: Measure[C, N])
    extends Measure[C, (M, N)] {

    override def toString = s"($m1 zip $m2)"

    def zero: (M, N) = (m1.zero, m2.zero)

    def apply(c: C): (M, N) = (m1.apply(c), m2.apply(c))

    def |+|(a: (M, N), b: (M, N)): (M, N) = (m1.|+|(a._1, b._1), m2.|+|(a._2, b._2))

    override def |+|(a: (M, N), b: (M, N), c: (M, N)): (M, N) = (m1.|+|(a._1, b._1, c._1), m2.|+|(a._2, b._2, c._2))
  }
}

trait Measure[-C, M] {
  import Measure._

  def zero: M

  def apply(c: C): M

  def |+|(a: M, b: M): M

  def |+|(a: M, b: M, c: M): M = |+|(|+|(a, b), c)

  final def zip[C1 <: C, N](m: Measure[C1, N]): Measure[C1, (M, N)] = new Zip(this, m)
}
