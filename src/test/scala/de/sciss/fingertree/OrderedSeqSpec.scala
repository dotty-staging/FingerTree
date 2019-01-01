package de.sciss.fingertree

import org.scalatest.FunSpec

class OrderedSeqSpec extends FunSpec {
  describe("An OrderedSeq") {
    val input = Seq(9, 59, 10, 30, 70)
    val r1 = OrderedSeq(input: _*)

    it("should be correctly sorted") {
      assert (r1.nonEmpty)
      val li = r1.toList
      assert(li === input.sorted)
    }

    it("should be respond to positive includes") {
      input.foreach { p =>
        val c   = r1.includes(p)
        val opt = r1.get     (p)
        assert (c)
        assert (opt.contains(p))
      }
    }

    it("should be respond to negative includes") {
      val neg = (input.min - 1 to input.max + 1).filterNot(input.contains)
      neg.foreach { p =>
        val c   = r1.includes(p)
        val opt = r1.get     (p)
        assert (!c)
        assert (opt.isEmpty)
      }
    }

    it("should be respond to removals (positive, no duplicates)") {
      input.foreach { p =>
        val res = r1.removeAll(p)
        val li  = res.toList
        val cmp = input.sorted.filterNot(_ == p)
        assert (li == cmp)
      }
    }

    it("should be respond to removals (positive, duplicates)") {
      input.foreach { q =>
        val dup   = input :+ q
        val dupS  = dup.sorted
        val r2    = r1 + q
        dup.foreach { p =>
          val res = r2.removeAll(p)
          val li  = res.toList
          val cmp = dupS.filterNot(_ == p)
          assert (li == cmp)
        }
      }
    }

    it("should be respond to removals (negative)") {
      val neg = (input.min - 1 to input.max + 1).filterNot(input.contains)
      val cmp = input.sorted
      neg.foreach { p =>
        val res = r1.removeAll(p)
        val li  = res.toList
        assert (li == cmp)
      }
    }

    it("should be respond to ceil") {
      val all = (input.min - 1) to (input.max + 1)
      val inputS = input.sorted
      all.foreach { p =>
        val res     = r1.ceil(p)
        val allCeil = inputS.dropWhile(_ < p)
        val cmp     = allCeil.headOption
        assert (res == cmp)
      }
    }

    it("should be respond to floor") {
      val all     = (input.min - 1) to (input.max + 1)
      val inputS  = input.sorted
      all.foreach { p =>
        val res       = r1.floor(p)
        val allFloor  = inputS.takeWhile(_ <= p)
        val cmp       = allFloor.lastOption
        assert (res == cmp)
      }
    }

    it("should be respond to ceilIterator") {
      val all     = (input.min - 1) to (input.max + 1)
      val inputS  = input.sorted
      all.foreach { p =>
        val res       = r1.ceilIterator(p)
        val li        = res.toList
        val i         = inputS.indexWhere(_ >= p)
        val cmp = if (i >= 0) inputS.drop(i) else Nil
        assert (li == cmp)
      }
    }

    it("should be respond to floorIterator") {
      val all     = (input.min - 1) to (input.max + 1)
      val inputS  = input.sorted
      all.foreach { p =>
        val res       = r1.floorIterator(p)
        val li        = res.toList
        // XXX TODO --- this is ugly
        val i         = inputS.indexWhere(_ >= p)
        val cmp = if (i >= 0) {
          val x = inputS(i)
          if (x == p || i == 0) inputS.drop(i)
          else {
            val y = inputS(i - 1)
            val j = inputS.indexOf(y)
            inputS.drop(j)
          }
        } else {
          inputS.takeRight(1)
        }
        assert (li == cmp)
      }
    }

    it("should allow duplicate insertions") {
      val inputS  = input.sorted
      input.foreach { p =>
        val r2        = r1 + p
        val res       = r2.toList
        val cmp       = inputS.patch(inputS.indexOf(p), p :: Nil, 0)
        assert (res == cmp)
      }
    }

    it("should allow to remove all instances of an element") {
      val inputS  = input.sorted
      input.foreach { p =>
        val r2        = r1 + p
        val r3        = r2.removeAll(p)
        val res       = r3.toList
        val cmp       = inputS.patch(inputS.indexOf(p), Nil, 1)
        assert (res == cmp)
      }
    }
  }
}