package scalaz

import std.AllInstances._
import scalaz.scalacheck.ScalazProperties._
import scalaz.scalacheck.ScalazArbitrary._
import scala.util.Random

class DievTest extends Spec {
  val random = new Random()

  "toEStream" ! check {
    list: List[Int] =>
    val diev = Diev.fromValuesSeq(list)
    diev.toEStream.toList must be_===(diev.toList)
  }

  "length" ! check {
    set: Set[Int] =>
    val list = set.toList.sorted
    val diev = Diev.fromValuesSeq(list)
    diev.length must be_===(list.length)
  }

  "foldRight" ! check {
    set: Set[Int] =>
    val list = set.toList.sorted
    val diev = Diev.fromValuesSeq(list)
    diev.foldRight(List[Int]())(_ :: _) must be_===(list.foldRight(List[Int]())(_ :: _))
  }

  "insert order makes no difference" ! check {
    (list: List[Int]) => {
      val shuffledList = random.shuffle(list)
      val dievFromList = list.foldLeft(Diev.empty[Int])(_ + _)
      val dievFromShuffledList = shuffledList.foldLeft(Diev.empty[Int])(_ + _)
      dievFromList must be_===(dievFromShuffledList)
    }
  }

  "fixIntervalOrder" ! check {
    (tuple: (Int, Int)) => {
      val expectedResult = if (tuple._1 > tuple._2) tuple.swap else tuple
      DievInterval.fixIntervalOrder(tuple) must be_===(expectedResult)
    }
  }

  // TODO: Use data table to test subtractInterval.

  "fromValuesSeq / toSet" ! check {
    (set: Set[Int]) => Diev.fromValuesSeq(set.toSeq).toSet must be_===(set)
  }

  "fromValuesSeq / toList" ! check {
    (list: List[Int]) => {
      val sortedList = list.toSet.toList.sorted
      Diev.fromValuesSeq(list).toList must be_===(sortedList)
    }
  }

  "++ associativity" ! check {
    (first: Diev[Int], second: Diev[Int]) => first ++ second must be_===(second ++ first)
  }

  "intervals / fromIntervalsSeq" ! check {
    (original: Diev[Int]) => Diev.fromIntervalsSeq(original.intervals) must be_===(original)
  }

  "-- / ++" ! check {
    (first: Diev[Int], second: Diev[Int]) => first -- second ++ second must be_===(first ++ second)
  }

  checkAll(equal.laws[Diev[Int]])
  checkAll(monoid.laws[Diev[Int]])

  {
    import org.scalacheck._
    val listArb = Arbitrary(Gen.listOf(Gen.choose(-1000, 1000)))
    implicit val dievArb = dievArbitrary[Int](listArb, implicitly)
    checkAll(foldable.laws[Diev])
  }
}
