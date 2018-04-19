package com.twitter.config.parser

import CommonParsers._
import ConfigEntryParser._

object ConfigParser {

  def parseFile(configFileLines: Iterator[String],
                overrides: Seq[String] = Nil): Map[String, Any] =
    configFileLines
      .map(_.trim)
      .foldLeft(ParserState.empty)(parseLine(overrides))
      .config

  private def parseLine(overrides: Seq[String])(parserState: ParserState,
                                                line: String): ParserState =
    skipEmptyLines
      .orElse(skipComment)
      .orElse(parseGroupName)
      .orElse(parseConfigEntry(overrides))
      .orElse(skipLine)
      .apply((parserState, line))

}
