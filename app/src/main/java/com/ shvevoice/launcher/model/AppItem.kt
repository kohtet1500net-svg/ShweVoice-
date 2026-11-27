package com.shwevoice.launcher.model

data class AppItem(
    val id: String,
    val name: String,
    val burmeseName: String,
    val packageName: String,
    val aliases: List<String>
)

enum class CommandAction {
    OPEN_APP,
    CLOSE_APP,
    CLOSE_ALL_APPS,
    GO_HOME,
    UNKNOWN
}

data class ParsedBurmeseCommand(
    val action: CommandAction,
    val targetApp: AppItem?,
    val spokenFeedback: String,
    val rawTranscript: String
)
