package com.github.ronkigen.intellijaicompletionplugin.completion

data class AICodeSuggestion(
    val code: String,
    val displayText: String,
    val confidence: Float,
    val type: SuggestionType = SuggestionType.COMPLETION
)

enum class SuggestionType {
    COMPLETION,
    FUNCTION,
    CLASS,
    COMMENT,
    IMPORT
}
