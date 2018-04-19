package com.twitter.config.parser

import scala.util.Try

private object EntryValueParser {

  /**
    * This function trying to understand type of value explicitly and if its not possible, tries to parse it in all possible types.
    */
  def parseEntryValue(value: String): Any =
    parseExplicitStringValue
      .orElse(parseExplicitArrayValue)
      .orElse(parseExplicitBooleanValue)
      .orElse(parseAmbiguousValue)
      .apply(stripLineFromComments(value))

  private def stripLineFromComments(line: String): String =
    line.split(';').head

  private def parseExplicitStringValue: PartialFunction[String, Any] = {
    case str if str.head == '"' && str.last == '"' =>
      str.substring(1, str.length - 1)
  }

  private def parseExplicitArrayValue: PartialFunction[String, Any] = {
    case arr if arr.head == '[' && arr.last == ']' =>
      arr.substring(1, arr.length - 1).split(',').map(parseEntryValue).toSeq
  }

  private def parseExplicitBooleanValue: PartialFunction[String, Any] = {
    case bool if bool == "yes" || bool == "true" => true
    case bool if bool == "no" || bool == "false" => false
  }

  /**
    * In case if its not possible to understand which type of value is we just trying to parse it as Number or Array
    * and if both doesn't fit, then we give up and mark it as a String.
    */
  private def parseAmbiguousValue: PartialFunction[String, Any] = {
    case whatever =>
      tryToParseNumber(whatever)
        .orElse(tryToParseArray(whatever))
        .getOrElse(giveUpAndParseString(whatever))
  }

  /**
    * Because there is no information in schema about type of number, we just bruteforce it...
    */
  private def tryToParseNumber(whatever: String): Option[Any] =
    Try(whatever.toInt)
      .orElse(Try(whatever.toLong))
      .orElse(Try(whatever.toDouble))
      .toOption

  private def tryToParseArray(whatever: String): Option[Seq[Any]] =
    Some(whatever)
      .filter(_.contains(','))
      .map(arrayStr => arrayStr.split(',').map(parseEntryValue))

  private def giveUpAndParseString(whatever: String): String =
    whatever.trim()

}
