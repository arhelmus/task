package com.twitter.config

import org.scalatest.{Matchers, WordSpec}
import MapToClassConverter._
import shapeless.{HList, LabelledGeneric, Lazy}

class MapToClassConverterTest extends WordSpec with Matchers {

  "Map to class converter" should {

    "convert basic types" in new Context {
      case class BasicTypesTestClass(a: Int,
                                     b: String,
                                     c: Boolean,
                                     d: Char,
                                     f: Byte,
                                     e: Double)
      val testMap = Map(
        "a" -> 42,
        "b" -> "test",
        "c" -> true,
        "d" -> 'a',
        "f" -> 42.toByte,
        "e" -> 42.42
      )

      convertMapTo[BasicTypesTestClass].from(testMap) shouldBe Some(
        BasicTypesTestClass(42, "test", true, 'a', 42, 42.42))
    }

    "convert nested classes" in new Context {
      case class NestedTestClass(name: String)
      case class NestedClassesTestClass(nested: NestedTestClass)
      val testMap = Map(
        "nested" -> Map("name" -> "Arthur")
      )

      convertMapTo[NestedClassesTestClass].from(testMap) shouldBe Some(
        NestedClassesTestClass(NestedTestClass("Arthur")))
    }

    "convert lists" in new Context {
      case class NestedListClassTestClass(nestedList: Seq[String])
      val testMap = Map(
        "nestedList" -> Seq("Arthur")
      )

      convertMapTo[NestedListClassTestClass].from(testMap) shouldBe Some(
        NestedListClassTestClass(Seq("Arthur")))
    }

    "be unstrict to redundant values in map" in new Context {
      case class StrictnessTestClass(b: String)
      val testMap = Map(
        "a" -> 42,
        "b" -> "test",
        "c" -> true,
        "d" -> 'a',
        "f" -> 42.toByte,
        "e" -> 42.42
      )

      convertMapTo[StrictnessTestClass].from(testMap) shouldBe Some(
        StrictnessTestClass("test"))
    }

    "return None if types are unmatch" in new Context {
      case class TypeUnmatchTestClass(a: String)
      val testMap = Map(
        "a" -> 42
      )

      convertMapTo[TypeUnmatchTestClass].from(testMap) shouldBe None
    }

    "return None if field does not exists" in new Context {
      case class FieldExistenceTestClass(a: String)
      val testMap = Map(
        "b" -> "test",
        "c" -> true,
        "d" -> 'a',
        "f" -> 42.toByte,
        "e" -> 42.42
      )

      convertMapTo[FieldExistenceTestClass].from(testMap) shouldBe None
    }

  }

  trait Context {
    class FromMapOps[A] {
      def from[R <: HList](m: Map[String, Any])(
          implicit
          gen: LabelledGeneric.Aux[A, R],
          fromMap: Lazy[FromMap[R]]): Option[A] = fromMap.value(m).map(gen.from)
    }

    def convertMapTo[A]: FromMapOps[A] = new FromMapOps[A]
  }

}
