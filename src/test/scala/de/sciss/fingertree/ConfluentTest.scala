package de.sciss.fingertree

object ConfluentTest extends App {
  implicit val m: Measure[(Int, Int), (Int, Long)] = new Measure[(Int, Int), (Int, Long)] {
    def zero = (0, 0L)
    def |+|(a: (Int, Long), b: (Int, Long)) = (a._1 + b._1, a._2 + b._2)

    def apply(c: (Int, Int)) = (1, c._2.toLong)
  }

  val t0 = FingerTree((0,3), (1,5), (3,11), (4,13))(m)
  println(t0)
  val t1 = t0 :+ (5,17)
  println(t1)
  val t2 = t1 :+ (5,17)
  println(t2)
  val t3 = t2 :+ (6, 19) :+ (7, 23) :+ (8, 29) :+ (9, 31) :+ (10, 37) :+ (11, 41) :+ (12, 43) :+ (13, 47) :+
    (14, 53) :+ (15, 59) :+ (16, 61) :+ (17, 67) :+ (18, 71) :+ (19, 73) :+ (20, 79) :+ (21, 83) :+ (22, 89)
  println(t3)
}