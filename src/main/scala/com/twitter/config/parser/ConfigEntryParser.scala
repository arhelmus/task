package com.twitter.config.parser

import EntryValueParser._

private object ConfigEntryParser {

  /**
    * Checks is entry are eligible for current overrides and parses it.
    */
  def parseConfigEntry(overrides: Seq[String]): ParsingFunc = {
    case (state, line) if isEntry(line) =>
      val (rawKey, rawValue) = separateKeyFromValue(line)
      val (parsedKey, environment) = extractEnvironmentFromKey(rawKey)

      if (checkEntryForOverrides(environment, overrides)) {
        state.addNewValueToProcessingGroup(toLowerCamelCase(parsedKey),
                                           parseEntryValue(rawValue))
      } else {
        state
      }
  }

  private def isEntry(line: String): Boolean =
    line.contains('=')

  private def extractEnvironmentFromKey(
      key: String): (String, Option[String]) = {
    val indexOfOverrideStart = key.indexOf('<')
    val indexOfOverrideEnd = key.indexOf('>')

    if (indexOfOverrideStart != -1 && indexOfOverrideStart < indexOfOverrideEnd) {
      val cleanKey =
        key.substring(0, indexOfOverrideStart)
      val environment =
        Some(key.substring(indexOfOverrideStart + 1, indexOfOverrideEnd))

      (cleanKey, environment)
    } else {
      (key, None)
    }
  }

  private def separateKeyFromValue(line: String): (String, String) = {
    val separatedLine = line.splitAt(line.indexOf('='))
    separatedLine._1.trim() -> separatedLine._2.drop(1).trim()
  }

  private def checkEntryForOverrides(entryEnvironment: Option[String],
                                     overrides: Seq[String]): Boolean =
    entryEnvironment.isEmpty || entryEnvironment.exists(overrides.contains)

}
