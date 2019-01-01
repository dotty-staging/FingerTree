package de.sciss.fingertree

import language.implicitConversions

object OrderedSeqTest extends App {
  val sym = Seq(("Cs", 55), ("Fr", 87), ("K", 19), ("Li", 3), ("Na", 11), ("Rb", 37))
  val sq  = OrderedSeq(sym: _*)(_._2, Ordering.Int)
  val li  = sq.toList // List((Li,3), (Na,11), (K,19), (Rb,37), (Cs,55), (Fr,87))
  val ceil20  = sq.ceilIterator  (20).toList  // List((Rb,37), (Cs,55), (Fr,87))
  val floor20 = sq.floorIterator (20).toList  // List((K,19), (Rb,37), (Cs,55), (Fr,87))
}