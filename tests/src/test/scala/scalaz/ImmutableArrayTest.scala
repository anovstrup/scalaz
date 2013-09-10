package scalaz

import scalaz.scalacheck.ScalazProperties._
import scalaz.scalacheck.ScalazArbitrary._
import std.AllInstances._

class ImmutableArrayTest extends Spec {

  checkAll(equal.laws[ImmutableArray[Int]])
  checkAll(foldable.laws[ImmutableArray])
  checkAll(plus.laws[ImmutableArray])

}
