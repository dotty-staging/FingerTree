# FingerTree

[![Build Status](https://github.com/Sciss/FingerTree/workflows/Scala%20CI/badge.svg?branch=main)](https://github.com/Sciss/FingerTree/actions?query=workflow%3A%22Scala+CI%22)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/fingertree_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/fingertree_2.13)

## statement

FingerTree is an immutable sequence data structure in Scala programming language, offering O(1) prepend and 
append, as well as a range of other useful properties [^1]. Finger trees can be used as building blocks for 
queues, double-ended queues, priority queues, indexed and summed sequences.

FingerTree is (C)opyright 2011&ndash;2020 by Hanns Holger Rutz. All rights reserved. It is released under 
the [GNU Lesser General Public License](https://git.iem.at/sciss/FingerTree/raw/main/LICENSE) v2.1+ and comes 
with absolutely no warranties. To contact the author, send an e-mail to `contact at sciss.de`.

The current implementation is a rewrite of previous versions. It tries to combine the advantages of the finger 
tree found in Scalaz (mainly the ability to have reducers / measures) and of the finger tree implementation by 
Daniel Spiewak (small, self-contained, much simpler and faster), but also has a more idiomatic Scala interface 
and comes with a range of useful applications, such as indexed and summed sequences.

[^1] Hinze, R. and Paterson, R., Finger trees: a simple general-purpose data structure, Journal of Functional 
     Programming, vol. 16 no. 2 (2006), pp. 197--217

## linking

The following dependency is necessary:

    "de.sciss" %% "fingertree" % v

The current version `v` is `"1.5.5"`.

## building

This builds with sbt against Scala 2.13, 2.12, Dotty (JVM) and Scala 2.13 (JS). 
The last version to support Scala 2.11 is v1.5.4.

Standard targets are `compile`, `package`, `doc`, `console`, `test`, `publishLocal`.

## contributing

Please see the file [CONTRIBUTING.md](CONTRIBUTING.md)

## using

You can either implement your own data structure by wrapping a plain `FingerTree` instance. 
Trait `FingerTreeLike` can be used as a basis, it has two abstract methods `tree` and `wrap` which would need to 
be implemented.

Or you can use any of the provided ready-made data structures, such as `IndexedSeq` or `IndexedSummedSeq`. While 
the former might not be particularly interesting, as it does not add any functionality that is not found already 
in Scala's own immutable `IndexedSeq` (i.e. `Vector`), the latter provides the additional feature of measuring not 
just the indexed positions of the tree elements, but also an accumulative "sum" of any sort.

The core element for new structures is to provide an instance of `Measure` which is used by the finger tree to 
calculate the annotated meta data of the elements. The measure provides a `zero` value, a `unit` method which 
measures exactly one element, and a summation method `|+|` which accumulates measured data. To work correctly 
with the caching mechanism of the finger tree, `|+|` must be associative, i.e. `(a |+| b) |+| c = a |+| (b |+| c)`.

Future versions will provide more ready-made structures, such as ordered sequences and interval sequences. In the 
meantime, you can check out the previous Scalaz based version of this project at git tag `Scalaz`, which includes 
those structures.

### Indexed and summed sequence

A sequence that has efficient element look-up (random access), and additionally integrates its elements
(a running summation).

```scala
import de.sciss.fingertree._

implicit val m = Measure.SummedIntInt
val sq = IndexedSummedSeq[Int,Int]((1 to 10).map(i => i * i): _*)
sq.sum  // result: 385
sq.sumUntil(sq.size/2)  // result: 55
```

### Ranged sequence

A sequence of elements indexed by intervals. Allows for interval searches such as overlaps and intersections.

```scala
import de.sciss.fingertree._

val sq = RangedSeq(
  (1685, 1750) -> "Bach",
  (1866, 1925) -> "Satie",
  (1883, 1947) -> "Russolo",
  (1883, 1965) -> "Varèse",
  (1910, 1995) -> "Schaeffer",
  (1912, 1992) -> "Cage"
)(_._1, Ordering.Int)

implicit class Names(it: Iterator[(_, _)]) {
  def names = it.map(_._2).mkString(", ")
}

sq.intersect(1900).names               // were alive in this year: Satie, Varèse, Russolo
sq.filterIncludes(1900 -> 1930).names  // were alive during these years: Varèse, Russolo
sq.filterOverlaps(1900 -> 1930).names  // were alive at some point of this period: all but Bach
```

### Ordered sequence

An ordered sequence that allows to find closest (floor or ceil) elements and create partial iterators.

```scala
import de.sciss.fingertree._

val sym = Seq(("Cs", 55), ("Fr", 87), ("K", 19), ("Li", 3), ("Na", 11), ("Rb", 37))
val sq  = OrderedSeq(sym: _*)(_._2, Ordering.Int)
val li  = sq.toList // List((Li,3), (Na,11), (K,19), (Rb,37), (Cs,55), (Fr,87))
val ceil20  = sq.ceilIterator  (20).toList  // List((Rb,37), (Cs,55), (Fr,87))
val floor20 = sq.floorIterator (20).toList  // List((K,19), (Rb,37), (Cs,55), (Fr,87))
```

## todo

 - efficient bulk loading
 - (an max-priority-queue -- less interesting though, because there are already good structures in standard scala collections)
 - proper `equals` and `hashCode` methods
 - `RangedSeq`: element removal
