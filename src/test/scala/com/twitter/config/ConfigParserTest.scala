package com.twitter.config

import com.twitter.config.parser.ConfigParser
import org.scalatest.{Matchers, WordSpec}

class ConfigParserTest extends WordSpec with Matchers {

  def parseConfig(config: String, overrides: Seq[String] = Nil) =
    ConfigParser.parseFile(config.split('\n').toIterator, overrides)

  "Config file parser" should {

    "parse multiple groups" in {
      val testConfig =
        """
          |[main]
          |[test_group]
          |[main_group]
        """.stripMargin

      val expectedResult = Map(
        "main" -> Map.empty[String, Any],
        "testGroup" -> Map.empty[String, Any],
        "mainGroup" -> Map.empty[String, Any]
      )

      parseConfig(testConfig) shouldBe expectedResult
    }

    "convert names of fields and groups to camel-case" in {
      val testConfig =
        """
          |[main_group]
          |test_value = blabla
        """.stripMargin

      parseConfig(testConfig) shouldBe Map(
        "mainGroup" -> Map("testValue" -> "blabla"))
    }

    "ignore comments" in {
      val testConfig =
        """
          |; blabla
          |[main]
          |; blabla
          |test = blabla;blabla
        """.stripMargin

      parseConfig(testConfig) shouldBe Map("main" -> Map("test" -> "blabla"))
    }

    "parse numbers" in {
      val testConfig =
        """
          |[main]
          |number = 42
        """.stripMargin

      parseConfig(testConfig) shouldBe Map("main" -> Map("number" -> 42))
    }

    "parse strings" in {
      val testConfig =
        """
          |[main]
          |string = blabla
          |string2 = blabla
        """.stripMargin

      parseConfig(testConfig) shouldBe Map(
        "main" -> Map("string" -> "blabla", "string2" -> "blabla"))
    }

    "parse booleans" in {
      val testConfig =
        """
          |[main]
          |bool1 = yes
          |bool2 = no
          |bool3 = true
          |bool4 = false
        """.stripMargin

      parseConfig(testConfig) shouldBe Map(
        "main" -> Map("bool1" -> true,
                      "bool2" -> false,
                      "bool3" -> true,
                      "bool4" -> false))
    }

    "parse lists" in {
      val testConfig =
        """
          |[main]
          |list = [a, b, c, d]
          |list2 = a,b,c,d
        """.stripMargin

      parseConfig(testConfig) shouldBe Map(
        "main" -> Map("list" -> Seq("a", "b", "c", "d"),
                      "list2" -> Seq("a", "b", "c", "d")))
    }

    "parse overrides" in {
      val testConfig =
        """
          |[main]
          |a = test
          |a<ov1> = ov1
          |a<ov2> = ov2
        """.stripMargin

      parseConfig(testConfig) shouldBe Map("main" -> Map("a" -> "test"))
      parseConfig(testConfig, Seq("ov1")) shouldBe Map(
        "main" -> Map("a" -> "ov1"))
      parseConfig(testConfig, Seq("ov2")) shouldBe Map(
        "main" -> Map("a" -> "ov2"))
    }

  }

}
