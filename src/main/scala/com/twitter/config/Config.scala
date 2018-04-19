package com.twitter.config

import com.twitter.config.MapToClassConverter._
import com.twitter.config.parser.ConfigParser
import ConfigParser._
import shapeless._

import scala.io.{Codec, Source}

object Config {

  /**
    * Summoning function for shapeless, config loader has apply method.
    * In the end customer should not see that when method is called there two different functions executed.
    */
  def load[A]: ConfigLoader[A] =
    new ConfigLoader[A]

}

class ConfigLoader[A] {

  def apply[R <: HList](path: String, overrides: Seq[String])(
      implicit gen: LabelledGeneric.Aux[A, R],
      fromMap: Lazy[FromMap[R]]): A = {

    (loadFile _)
      .andThen(parseFile(_, overrides))
      .andThen(convertMapToConfig[A, R])
      .andThen(handleConversionResult)
      .apply(path)
  }

  private def loadFile(path: String): Iterator[String] =
    Source.fromFile(path)(Codec.UTF8).getLines()

  private def convertMapToConfig[A, R <: HList](map: Map[String, Any])(
      implicit gen: LabelledGeneric.Aux[A, R],
      fromMap: Lazy[FromMap[R]]): Option[A] = {

    fromMap.value(map).map(gen.from)
  }

  private def handleConversionResult[A](conversionResult: Option[A]): A =
    conversionResult match {
      case Some(result) => result
      case None         => throw new RuntimeException("Unable to parse config file...")
    }

}
