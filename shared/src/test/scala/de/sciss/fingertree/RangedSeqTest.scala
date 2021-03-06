package de.sciss.fingertree

import scala.language.implicitConversions

object RangedSeqTest extends App {
  object Region {
    implicit def view(r: Region): (Int, Int) = (r.start, r.stop)
  }
  case class Region(name: String, start: Int, stop: Int)
  val r0  = RangedSeq.empty[Region, Int]
  val r1  = r0 + Region("a", 10, 20)
  val r2  = r1 + Region("b", 2, 3)

  val res = r2.findOverlaps((0,3))
  println(res)
}