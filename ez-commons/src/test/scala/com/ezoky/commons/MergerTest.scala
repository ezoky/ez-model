package com.ezoky.commons

import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 04/03/2021
  * @since 0.2.0
  */
class MergerTest
  extends AnyFunSuite
with Mergers {

  test("Merging is contextual") {

    case class Point(x: Double, y: Double)

    {
      // Merging two points is taking the middle of them
      implicit val Middle: Merger[Point] =
        Merger.define((point1, point2) => Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2))

      assert(Point(1, 2).mergeWith(Point(1, 2)) === Point(1, 2))
      assert(Point(1, 2).mergeWith(Point(2, 3)) === Point(1.5, 2.5))
    }

    {
      // Merging two points is taking the farthest from origin
      implicit val Farthest: Merger[Point] =
        Merger.define((point1, point2) => {
           def d(p1: Point, p2: Point): Double =
             math.sqrt(math.pow(p2.x - p1.x, 2) + math.pow(p2.y - p1.y, 2))

          if (d(Point(0,0), point1) > d(Point(0,0), point2)) {
            point1
          }
          else {
            point2
          }
        })

      val middle = Point(1, 2).mergeWith(Point(2, 3))

      assert(Point(1, 2).mergeWith(Point(1, 2)) === Point(1, 2))
      assert(Point(1, 2).mergeWith(Point(-2, -3)) === Point(-2, -3))
    }

  }

}
