package de.sciss.fingertree

import org.scalatest.funspec.AnyFunSpec

/**
 * test-only de.sciss.fingertree.IndexedSummedSeqSpec
 */
class IndexedSummedSeqSpec extends AnyFunSpec {
  describe("An IndexedSummedSeq") {
    implicit val m = Measure.SummedIntInt
    val li = (1 to 10).map(i => i * i).toIndexedSeq
    val sq = IndexedSummedSeq[Int,Int](li: _*)

    it("should be reporting correct indices") {
      (0 until li.size).foreach { idx =>
        assert(sq(idx) === li(idx))
      }
      assert(sq.size === li.size)
    }

    it("should be reporting correct sums") {
      (0 until li.size).foreach { idx =>
        assert(sq.sumUntil(idx) === li.take(idx).sum)
      }
      assert(sq.sumUntil(li.size) === sq.sum)
      assert(sq.sum === li.sum)
    }
  }
}