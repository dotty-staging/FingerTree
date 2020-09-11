package de.sciss.fingertree

import org.scalatest.funspec.AnyFunSpec

import scala.annotation.tailrec

/**
 * test-only de.sciss.fingertree.RangedSeqSpec
 */
class RangedSeqSpec extends AnyFunSpec {
  describe("A RangedSeq") {
    val r1 = RangedSeq(
      (9,10),
      (59,61),
      (10,20),
      (30,40),
      (70,80)
    )
    val r2 = RangedSeq(
      (10,20),
      (11,19),
      (12,18),
      (17,21),
      (30,40),
      (35,45)
    )

    it("should be correctly sorted") {
      val li = r1.toList
      assert(li === List((9,10), (10,20), (30,40), /* (31,39), */ /* (50,60), */ (59,61), (70,80)))
    }

    it("should find its own elements") {
      r1.toList.foreach { ival =>
        val found = r1.findOverlaps(ival)
        assert(found === Some(ival))
      }
    }

    it("should not find overlaps which are gaps in the data set") {
      val rl    = (-1, 0) :: r1.toList ::: List((100, 101))
      val gaps  = rl.sliding(2,1).map {
        case (_, stop) :: (start, _) :: Nil => (stop, start)
        case _ => sys.error("Unexpected match")
      }
      gaps.foreach { ival =>
        val found = r1.findOverlaps(ival)
        assert(found.isEmpty, s"found = $found for gap $ival")
      }
    }

    it("should find overlaps with empty ranges") {
      val r3 = RangedSeq((8,8))
      assert(r3.findOverlaps((7,8)) === None)
      assert(r3.findOverlaps((8,9)) === None)
      assert(r3.findOverlaps((7,9)) === Some((8,8)))
    }

    it("should find multiple overlaps") {
      assert(r2.filterOverlaps(( 0, 10)).toList === Nil)
      assert(r2.filterOverlaps((22, 30)).toList === Nil)
      assert(r2.filterOverlaps((45,100)).toList === Nil)

      val res1 = r2.filterOverlaps(( 0, 12))
      assert(res1.toList === List((10,20), (11,19)))
      assert(r2.filterOverlaps(( 0, 13)).toList === List((10,20), (11,19), (12,18)))

      assert(r2.filterOverlaps((12, 13)).toList === List((10,20), (11,19), (12,18)))
      assert(r2.filterOverlaps((10,36)).toList === r2.toList)
      assert(r2.filterOverlaps((30,30)).toList === Nil)
      assert(r2.filterOverlaps((30,45)).toList === List((30,40), (35,45)))

    }

    it("should report its total range") {
      val rl  = r1.toList
      val min = rl.map(_._1).min
      val max = rl.map(_._2).max
      assert(r1.interval === Some((min, max)))
    }

    it("should return correct answers to some corner cases") {
      val r2 = RangedSeq((0,3),(1,2))
      val r3 = r2.filterOverlaps((2,3))
      assert(r3.toList === List((0,3)))
    }

    it("should answer inclusion queries") {
      assert(r1.toList.forall { ival => r1.filterIncludes(ival).nonEmpty })
      assert(r1.toList.forall { case (lo, hi) => r1.filterIncludes((lo + 1, hi)).nonEmpty })
      assert(r1.toList.forall { case (lo, hi) => r1.filterIncludes((lo, hi - 1)).nonEmpty })
      assert(r1.toList.forall { case (lo, hi) => r1.filterIncludes((lo - 1, hi)).isEmpty })
      assert(r1.toList.forall { case (lo, hi) => r1.filterIncludes((lo, hi + 1)).isEmpty })
    }

    it("should support deletion") {
      type Elem = (Char, Int, Int)
      implicit val view: Elem => (Int, Int) = tup => (tup._2, tup._3)
      val elems = List(
        ('a', 10, 20),
        ('b', 10, 20),
        ('c', 10, 21),
        ('d', 11, 20),
        ('d', 10, 20),
        ('e', 22, 30)
      )
      val r3 = RangedSeq[Elem, Int](elems: _*)
      // remove single elements
      elems.foreach { e =>
        assert((r3 - e).iterator.toSet === elems.filterNot(_ == e).toSet)
      }
      // remove elements successively
      @tailrec def loop(list: List[Elem], tree: RangedSeq[Elem, Int]): Unit =
        list match {
          case head :: tail =>
            val treeRem = tree - head
            assert(treeRem.iterator.toSet === tail.toSet)
            loop(tail, treeRem)
          case _ =>
        }

      loop(elems, r3)
    }
  }
}