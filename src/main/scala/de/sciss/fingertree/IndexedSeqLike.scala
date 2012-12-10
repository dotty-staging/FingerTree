package de.sciss.fingertree

trait IndexedSeqLike[ V, A, Repr <: IndexedSeqLike[ V, A, Repr ]] extends FingerTreeLike[ V, A, Repr ] {
//   final def ++( xs: Repr ): Repr = wrap( tree <++> xs.tree )
   final def :+( x: A ): Repr = wrap( tree :+ x )
   final def +:( x: A ): Repr = wrap( x +: tree )

   final def apply( idx: Int ) : A = {
      if( idx < 0 || idx >= size ) throw new IndexOutOfBoundsException( idx.toString )
      tree.find1( indexPred( idx ))
   }

   def size : Int

   final def drop(      n: Int ) : Repr = wrap( dropTree( n ))
   final def dropRight( n: Int ) : Repr = wrap( takeTree( size - n ))
   final def slice( from: Int, until: Int ) : Repr = take( until ).drop( from )

   final def splitAt( i: Int ) : (Repr, Repr) = {
      val (l, r) = tree.split( indexPred( i ))
      (wrap( l ), wrap( r ))
   }

   final def take(      n: Int ) : Repr = wrap( takeTree( n ))
   final def takeRight( n: Int ) : Repr = wrap( dropTree( size - n ))

//   final def updated( index: Int, elem: A ) : Repr = {
//      if( index < 0 || index >= size ) throw new IndexOutOfBoundsException( index.toString )
//      val (l, _, r) = splitTree1( index )
//      wrap( l.:+( elem ).<++>( r ))  // XXX most efficient?
//   }

   protected def indexPred( i: Int ) : V => Boolean

   private def takeTree( i: Int ) = tree.takeWhile( indexPred( i ))
   private def dropTree( i: Int ) = tree.dropWhile( indexPred( i ))
//   private def splitTree1(  i: Int ) = tree.split1( indexPred( i ))
}