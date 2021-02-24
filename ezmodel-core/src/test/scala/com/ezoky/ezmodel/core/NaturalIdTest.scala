package com.ezoky.ezmodel.core

import com.ezoky.ezmodel.core.NaturalId.NaturalMap
import org.scalatest.funsuite.AnyFunSuite

/**
  * @author gweinbach on 24/02/2021
  * @since 0.2.0
  */
class NaturalIdTest
  extends AnyFunSuite {

  test("a Natural Id is contextual") {

    case class Point(x: Int, y: Int)

    {
      // On the plane
      implicit val PlanePos: NaturalId[Point] =
        NaturalId.define(t => (t.x, t.y))

      val points = NaturalMap(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 4)
    }

    {
      // On the abscissa
      implicit val AbscissaPos: NaturalId[Point] =
        NaturalId.define(_.x)

      val points = NaturalMap(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 2)
    }

    {
      // On the ordinate
      implicit val OrdinatePos: NaturalId[Point] =
        NaturalId.define(_.y)

      val points = NaturalMap(Point(1, 2), Point(2, 3), Point(1, 4), Point(2, 4), Point(1, 2))

      assert(points.size === 3)
    }
  }

}
