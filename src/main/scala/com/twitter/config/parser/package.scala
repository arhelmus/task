package com.twitter.config

import com.google.common.base.CaseFormat

package object parser {

  case class ParserState(config: Map[String, Map[String, Any]],
                         groupInProcess: String) {

    def addNewGroup(groupName: String): ParserState =
      this.copy(config = this.config + (groupName -> Map.empty),
                groupInProcess = groupName)

    def addNewValueToProcessingGroup(key: String, value: Any): ParserState = {
      val updatedGroup = this.config(groupInProcess) + (key -> value)
      val updatedConfig = this.config.updated(groupInProcess, updatedGroup)

      this.copy(config = updatedConfig)
    }

  }

  object ParserState {
    val empty = ParserState(Map.empty, "")
  }

  type ParsingFunc = PartialFunction[(ParserState, String), ParserState]

  def toLowerCamelCase(line: String): String =
    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line)

}
