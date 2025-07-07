package com.github.ronkigen.intellijaicompletionplugin.completion

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font

object AIInlineRenderer {
    private var currentHighlighter: RangeHighlighter? = null
    private var currentSuggestion: AICodeSuggestion? = null
    
    fun showInlineSuggestion(editor: Editor, suggestion: AICodeSuggestion) {
        clearCurrentSuggestion(editor)
        
        val offset = editor.caretModel.offset
        
        // Create ghost text attributes
        val attributes = TextAttributes().apply {
            foregroundColor = Color.GRAY
            fontType = Font.ITALIC
        }
        
        // Add inline suggestion as ghost text
        val highlighter = editor.markupModel.addRangeHighlighter(
            offset,
            offset,
            HighlighterLayer.LAST + 1,
            attributes,
            HighlighterTargetArea.EXACT_RANGE
        )
        
        // Custom renderer for ghost text
        highlighter.setGutterIconRenderer(object : com.intellij.openapi.editor.markup.GutterIconRenderer() {
            override fun getIcon() = AIIcons.SUGGESTION
            override fun equals(other: Any?) = false
            override fun hashCode() = 0
        })
        
        currentHighlighter = highlighter
        currentSuggestion = suggestion
    }
    
    fun clearCurrentSuggestion(editor: Editor) {
        currentHighlighter?.let {
            editor.markupModel.removeHighlighter(it)
            currentHighlighter = null
            currentSuggestion = null
        }
    }
    
    fun acceptCurrentSuggestion(editor: Editor): Boolean {
        val suggestion = currentSuggestion
        val highlighter = currentHighlighter
        
        if (suggestion != null && highlighter != null) {
            val offset = editor.caretModel.offset
            val document = editor.document
            
            // Insert the suggestion
            document.insertString(offset, suggestion.code)
            editor.caretModel.moveToOffset(offset + suggestion.code.length)
            
            // Clear the suggestion
            clearCurrentSuggestion(editor)
            return true
        }
        return false
    }
}

