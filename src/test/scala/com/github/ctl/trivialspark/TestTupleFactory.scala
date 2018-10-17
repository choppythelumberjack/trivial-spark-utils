package com.github.ctl.trivialspark

import org.scalatest.{FreeSpec, Matchers}
import TupleSumFactories.TupleSumFactory2
import org.apache.spark.sql.{Dataset, SparkSession}

object Tuple2Example {
  val factory2 = new TupleSumFactory2[String, Int]()
}

class TestTupleFactory extends FreeSpec with Matchers {
  val sparkSession =
    SparkSession
      .builder()
      .master("local")
      .appName("spark test")
      .getOrCreate()

  import sparkSession.implicits._

  "Tuple Factory 2" - {
    import Tuple2Example._

    val ds:Dataset[Tuple2Example.factory2.TupleSum] =
      Seq(
        factory2.construct1("Foo"),
        factory2.construct2(18),
        factory2.construct1("Bar"),
        factory2.construct2(36),
        factory2.construct2(9)
      ).toDS()

    "Filter Collection Case" in {
      val outputs =
        ds.filter(tup => tup._2 match {
          case Some(value) => value % 2 == 0
          case None => false
        }).collect()

      outputs.toList should equal(List(
        factory2.construct2(18),
        factory2.construct2(36)
      ))
    }

    import TupleSumExtensions._
    "Match Element Case" in {
      val outputs =
        ds.map(tup => tup.element match {
          case i:Int => s"I${i}"
          case s:String => s"S${s}"
        }).collect()

      outputs.toList should equal(List(
        "SFoo",
        "I18",
        "SBar",
        "I36",
        "I9"
      ))
    }

    "Fold Tuple" in {
      (Some("foo"), Option.empty[Int]).fold(
        str => s"S${str}",
        i => s"I${i}"
      ) should equal("Sfoo")

      (Option.empty[String], Some(18)).fold(
        str => s"S${str}",
        i => s"I${i}"
      ) should equal("I18")
    }
  }
}
