package de.sciss.fingertree

import org.scalatest.FunSpec

/**
 * test-only de.sciss.fingertree.RangedSeqSpec
 */
class RangedSeqSpec extends FunSpec {
  describe("A RangedSeq") {
    val r = RangedSeq((9,10), (59,61), (10,20), /* (31,39), */ (30,40), /* (50,60), */ (70,80))

    it("should be correctly sorted") {
      val li = r.toList
      assert(li === List((9,10), (10,20), (30,40), /* (31,39), */ /* (50,60), */ (59,61), (70,80)))
    }

    it("should find its own elements") {
      r.toList.foreach { ival =>
        val found = r.findOverlap(ival)
        assert(found === Some(ival))
      }
    }

    it("should not find overlaps which are gaps in the data set") {
      val rl    = (-1, 0) :: r.toList ::: List((100, 101))
      val gaps  = rl.sliding(2,1).map {
        case (_, stop) :: (start, _) :: Nil => (stop, start)
        case _ => sys.error("Unexpected match")
      }
      gaps.foreach { ival =>
        val found = r.findOverlap(ival)
        assert(found.isEmpty, s"found = $found for gap $ival")
      }
    }

    it("should find overlaps with empty ranges") {
      val r1 = RangedSeq((8,8))
      assert(r1.findOverlap((7,8)) === None)
      assert(r1.findOverlap((8,9)) === None)
      assert(r1.findOverlap((7,9)) === Some((8,8)))
    }

    it("should find multiple overlaps") {
      val r1 = RangedSeq(
        (10,20),
        (11,19),
        (12,18),
        (17,21),
        (30,40),
        (35,45)
      )
      assert(r1.filterOverlap(( 0, 10)).toList === Nil)
      assert(r1.filterOverlap((22, 30)).toList === Nil)
      assert(r1.filterOverlap((45,100)).toList === Nil)

      val res1 = r1.filterOverlap(( 0, 12))
      assert(res1.toList === List((10,20), (11,19)))
      assert(r1.filterOverlap(( 0, 13)).toList === List((10,20), (11,19), (12,18)))

      assert(r1.filterOverlap((12, 13)).toList === List((10,20), (11,19), (12,18)))
      assert(r1.filterOverlap((10,36)).toList === r1.toList)
      assert(r1.filterOverlap((30,30)).toList === Nil)
      assert(r1.filterOverlap((30,45)).toList === List((30,40), (35,45)))
    }
  }
}