package de.sciss.fingertree

object ConfluentTest extends App {
  implicit val m = new Measure[(Int, Int), (Int, Long)] {
    def zero = (0, 0L)
    def |+|(a: (Int, Long), b: (Int, Long)) = (a._1 + b._1, a._2 + b._2)

    def apply(c: (Int, Int)) = (1, c._2.toLong)
  }

  val t = FingerTree((0,3), (1,5), (3,11), (4,13))(m)
  println(t)
  val t1 = t :+ (5,17) :+ (5,17)
  println(t1)
}