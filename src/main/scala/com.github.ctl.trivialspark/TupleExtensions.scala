package com.github.ctl.trivialspark

object TupleExtensions {
  implicit class TupleExt(tup:Product) {
    def element =
      tup.productIterator
        .find(_.isInstanceOf[Some[_]]).get
        .asInstanceOf[Some[_]].get
  }
}
