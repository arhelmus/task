package com.twitter.config.parser

private object CommonParsers {

  val skipEmptyLines: ParsingFunc = {
    case (state, line) if line == null || line == "" => state
  }

  val skipComment: ParsingFunc = {
    case (state, line) if isComment(line) => state
  }

  val parseGroupName: ParsingFunc = {
    case (state, line) if isGroupName(line) =>
      state.addNewGroup(parseGroupName(line))
  }

  val skipLine: ParsingFunc = {
    case (state, _) => state
  }

  private def parseGroupName(line: String): String =
    toLowerCamelCase(line.substring(1, line.length - 1))

  private def isGroupName(line: String): Boolean =
    line.head == '[' && line.last == ']'

  private def isComment(line: String): Boolean =
    line.head == ';'

}
