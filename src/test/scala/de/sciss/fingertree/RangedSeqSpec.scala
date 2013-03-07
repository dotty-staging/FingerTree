package de.sciss.fingertree

import org.scalatest.FunSpec

/**
 * test-only de.sciss.fingertree.RangedSeqSpec
 */
class RangedSeqSpec extends FunSpec {
  describe("A RangedSeq") {
    val r = RangedSeq((9,10), (59,61), (10,20), (31,39), (30,40), (50,60), (70,79), (70,80))

    it("should be correctly sorted") {
      val li = r.toList
      assert(li === List((9,10), (10,20), (30,40), (31,39), (50,60), (59,61), (70,80), (70,79)))
    }
  }
}