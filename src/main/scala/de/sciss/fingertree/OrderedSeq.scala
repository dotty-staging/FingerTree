/*
 * OrderedSeq.scala
 * (FingerTree)
 *
 * Copyright (c) 2011-2018 Hanns Holger Rutz. All rights reserved.
 *
 * This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.fingertree

object OrderedSeq {
  def empty[Elem, P](implicit view: Elem => P, ordering: Ordering[P]): OrderedSeq[Elem, P] =
    new Impl(view, ordering) {
      protected val tree: FingerTree[Anno[P], Elem] = FingerTree.empty
    }

  def apply[Elem, P](xs: Elem*)(implicit view: Elem => P, ordering: Ordering[P]): OrderedSeq[Elem, P] = {
    xs.foldLeft(empty[Elem, P])(_ + _)
  }

  private type Anno[P]      = Option[P]
  private type FT[Elem, P]  = FingerTree[Anno[P], Elem]

  private abstract class Impl[Elem, P](view: Elem => P, ordering: Ordering[P])
    extends OrderedSeq[Elem, P] with Measure[Elem, Anno[P]] {
    seq =>

    protected val tree: FingerTree[Anno[P], Elem]   // making this a val helps in debugger

    // ---- measure ----

    protected implicit def m: Measure[Elem, Anno[P]] = this

    def zero          : Anno[P] = None
    def apply(c: Elem): Anno[P] = Some(view(c))

    def |+|(a: Anno[P], b: Anno[P]): Anno[P] = (a, b) match {
      case (_, None)  => a
      case _          => b
    }

    // ---- finger-tree-like ----

    protected def wrap(_tree: FT[Elem, P]): OrderedSeq[Elem, P] = new Impl(view, ordering) {
      protected val tree: FT[Elem, P] = _tree
    }

    // ---- ranged-seq ----

    def +(elem: Elem): OrderedSeq[Elem, P] = {
      val (l, r)  = splitTreeAt(view(elem))
      val res     = l ++ (elem +: r)
      wrap(res)
    }

    def removeAll(elem: Elem): OrderedSeq[Elem, P] = {
      val (l, r)  = tree.span(isGt  (view(elem)))
      val (_, rr) = r   .span(isGteq(view(elem)))
      wrap(l ++ rr)
    }

    def get(point: P): Option[Elem] =
      if (isEmpty) None else {
        val (_, elem, _) = tree.span1(isGt(point))
        val found = view(elem)
        if (ordering.equiv(point, found)) Some(elem) else None
      }

    def floor(point: P): Option[Elem] =
      if (isEmpty) None else {
        val (pre, elem, _) = tree.span1(isGt(point))
        val found = view(elem)
        val cmp = ordering.compare(found, point)
        // println(s"point = $point, pre = $pre, post = $post, elem = $elem; cmp = $cmp")
        if (cmp <= 0) Some(elem) else pre.lastOption
      }

    def ceil(point: P): Option[Elem] =
      if (isEmpty) None else {
        val (_, elem, _) = tree.span1(isGt(point))
        val found = view(elem)
        if (ordering.lt(found, point)) None else Some(elem)
      }

    def firstKey: P = view(tree.head)
    def lastKey : P = view(tree.last)

    def iteratorFrom(point: P): Iterator[Elem] = ceilIterator(point)

    def floorIterator(point: P): Iterator[Elem] =
      if (isEmpty) Iterator.empty else {
        val (pre, elem, post) = tree.span1(isGt(point))
        val found = view(elem)
        val cmp   = ordering.lteq(found, point)
        // println(s"point = $point, pre = $pre, post = $post, elem = $elem; cmp = $cmp")
        val res   = if (cmp || pre.isEmpty) elem +: post else pre.last +: elem +: post
        res.iterator
      }

    def ceilIterator(point: P): Iterator[Elem] =
      if (isEmpty) Iterator.empty else {
        val (_, elem, post) = tree.span1(isGt(point))
        val found = view(elem)
        val cmp   = ordering.lt(found, point)
        if (cmp) Iterator.empty else {
          val res = elem +: post
          res.iterator
        }
      }

    def includes(point: P): Boolean = get(point).isDefined

    // is the argument less than an element's point?
    @inline private def isLt  (k: P)(v: Anno[P]) = v.exists(p => ordering.lt  (k, p))
    // is the argument less than or equal to an element's point?
    @inline private def isLteq(k: P)(v: Anno[P]) = v.exists(p => ordering.lteq(k, p))
    // is the argument greater than an element's point?
    @inline private def isGt  (k: P)(v: Anno[P]) = v.exists(p => ordering.gt  (k, p))
    // is the argument greater than or equal to element's point?
    @inline private def isGteq(k: P)(v: Anno[P]) = v.exists(p => ordering.gteq(k, p))

    private def splitTreeAt(point: P) =
      tree.span(_.exists(p => ordering.lt(p, point)))

    override def toString: String = tree.iterator.mkString("OrderedSeq(", ", ", ")")
  }
}
sealed trait OrderedSeq[Elem, P] extends FingerTreeLike[Option[P], Elem, OrderedSeq[Elem, P]] {
  /** Adds a new element to the tree. */
  def +(elem: Elem): OrderedSeq[Elem, P]

  /** Removes all occurrences of an element from the tree. */
  def removeAll(elem: Elem): OrderedSeq[Elem, P]

  /** Finds an element at a given point.
    *
    * @param point  the query point
    * @return       the element at the point, or `None` if such an element does not exist.
    */
  def get(point: P): Option[Elem]

  /** Finds an element at a given point. If no element exists at the point, the closest
    * element before the point is returned.
    *
    * @param point  the query point
    * @return       the element at or before the point, or `None` if such an element does not exist.
    */
  def floor(point: P): Option[Elem]

  /** Finds an element at a given point. If no element exists at the point, the closest
    * element after the point is returned.
    *
    * @param point  the query point
    * @return       the element at or after the point, or `None` if such an element does not exist.
    */
  def ceil(point: P): Option[Elem]

  def firstKey: P

  def lastKey: P

  /** Creates an iterator beginning at the element at or before a given point.
    * If an element at the point exists, the iterator starts at that element, otherwise
    * it starts at the closest element before that point.
    *
    * Note that if multiple elements share the same point of the floor position, it is not guaranteed
    * that all those elements are at the beginning of the returned iterator.
    */
  def floorIterator(point: P): Iterator[Elem]

  /** Alias for `ceilIterator` */
  def iteratorFrom(point: P): Iterator[Elem]

  /** Creates an iterator beginning at the element at or after a given point.
    * If an element at the point exists, the iterator starts at that element, otherwise
    * it starts at the closest element after that point.
    *
    * Note that if multiple elements share the same point of the ceil position, it is not guaranteed
    * that all those elements are at the beginning of the returned iterator.
    */
  def ceilIterator(point: P): Iterator[Elem]

  /** Tests whether an element at a given point exists. */
  def includes(point: P): Boolean
}