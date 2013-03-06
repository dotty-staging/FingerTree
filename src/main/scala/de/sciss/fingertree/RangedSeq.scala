package de.sciss.fingertree

object RangedSeq {
  def empty[Elem, P](implicit view: Elem => (P, P), ordering: Ordering[P]): RangedSeq[Elem, P] =
    new Impl(view, ordering) {
      protected val tree = FingerTree.empty[Anno[P], Elem]
    }

  def apply[Elem, P](xs: Elem*)(implicit view: Elem => (P, P), ordering: Ordering[P]): RangedSeq[Elem, P] = {
    xs.foldLeft(empty[Elem, P])(_ + _)
  }

  private type Anno[P]      = Option[(P, P)]
  private type FT[Elem, P]  = FingerTree[Anno[P], Elem]

  private abstract class Impl[Elem, P](view: Elem => (P, P), ordering: Ordering[P])
    extends RangedSeq[Elem, P] with Measure[Elem, Anno[P]] {

    // ---- measure ----

    protected implicit def m: Measure[Elem, Anno[P]] = this

    def zero          : Anno[P] = None
    def apply(c: Elem): Anno[P] = Some(view(c))

    def |+|(a: Anno[P], b: Anno[P]): Anno[P]              = b orElse a
    def |+|(a: Anno[P], b: Anno[P], c: Anno[P]): Anno[P]  = c orElse b orElse a

    // ---- fingertreelike ----

    protected def wrap(_tree: FT[Elem, P]): RangedSeq[Elem, P] = new Impl(view, ordering) {
      protected val tree = _tree
    }

    // ---- rangedseq ----

    def +(elem: Elem): RangedSeq[Elem, P] = {
      // Should have Interval wrapper that does this check...
      // require( ord.lteq( i._1, i._2 ), "Upper interval bound cannot be less than lower bound : " + i )
      val (l, r) = splitTreeAt(view(elem))
      wrap(l ++ (elem +: r))
    }

    def findOverlap(interval: (P, P)): Option[Elem] = {
      tree.measure flatMap { case (_, tHi) =>
        val (iLo, iHi) = interval
        // if the search interval's low bound is smaller or equal than the tree's total up bound...
        if (ordering.lteq(iLo, tHi)) {
          // "gives us the interval x with the smallest low endpoint
          //  whose high endpoint is at least the low endpoint of the query interval"
          //
          // Note: n <= MInfty is always false. Since MInfty is equivalent to None
          //   in our implementation, we can write _.map( ... ).getOrElse( false )
          //   for this test
          val (_, x, _) = tree.span1(atleast(iLo) _, tree.measure)
          // "It then remains to check that low x <= high i"
          if (ordering.lteq(view(x)._1, iHi)) Some(x) else None
        } else None
      }
    }

//    def filterOverlap(interval: (P, P)): Iterator[Elem] = {
//      val (iLo, iHi) = interval
//
////      def matches(xs: FT): Iterator[(A, A)] = {
////        val v = xs.takeWhile(atleast(iLo) _).viewl
////        v.fold(Stream.empty, (x, xs0) => Stream.cons(x, matches(xs0)))
////      }
////      matches(tree.takeUntil(greater(iHi) _))
//      ???
//    }

    @inline private def atleast(k: P)(v: Anno[P]) = v.map(tup => ordering.gt(k, tup._2)).getOrElse(false)
//    @inline private def greater(k: P)(v: Anno[P]) = v.map(tup => ordering.gt(tup._1, k)).getOrElse(false)

    // "We order the intervals by their low endpoints"
    private def splitTreeAt(interval: (P, P)) = {
      val iLo = interval._1
      tree.span(_.map(tup => ordering.lt(tup._1, iLo)).getOrElse(false))
    }

    override def toString = tree.iterator.mkString("RangedSeq(", ", ", ")")
  }
}
trait RangedSeq[Elem, P] extends FingerTreeLike[Option[(P, P)], Elem, RangedSeq[Elem, P]] {
  def +(elem: Elem): RangedSeq[Elem, P]

  /* TODO:
        this should be renamed to findTouching
        and findOverlap should change the semantics
        from lteq to lt!
   */
  def findOverlap(interval: (P, P)): Option[Elem]

// XXX TODO:
//  def filterOverlap(interval: (P, P)): Iterator[Elem]
}
