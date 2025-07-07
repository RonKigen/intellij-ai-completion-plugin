package com.github.ronkigen.intellijaicompletionplugin.completion

import com.intellij.codeInsight.completion.CompletionService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ManualCompletionAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        
        // Check if AI completion is enabled
        val settings = AISettings.getInstance()
        if (!settings.isEnabled) {
            Messages.showInfoMessage(
                project,
                "AI code completion is disabled. Please enable it in Settings > AI Code Completion.",
                "AI Completion Disabled"
            )
            return
        }
        
        if (settings.apiKey.isEmpty()) {
            Messages.showInfoMessage(
                project,
                "Please configure your Gemini API key in Settings > AI Code Completion.",
                "API Key Required"
            )
            return
        }
        
        // Trigger manual AI completion
        triggerAICompletion(editor, project)
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.getData(CommonDataKeys.PROJECT)
        
        // Enable action only when editor is available and has focus
        e.presentation.isEnabled = editor != null && 
                                   project != null && 
                                   editor.contentComponent.hasFocus()
        
        // Update action text based on AI settings
        val settings = AISettings.getInstance()
        e.presentation.text = if (settings.isEnabled) {
            "AI Complete (Gemini)"
        } else {
            "AI Complete (Disabled)"
        }
    }
    
    private fun triggerAICompletion(editor: Editor, project: Project) {
        val offset = editor.caretModel.offset
        val document = editor.document
        
        // Get context for AI completion
        val contextBefore = getContextBefore(document, offset)
        val contextAfter = getContextAfter(document, offset)
        
        // Show loading indicator
        ApplicationManager.getApplication().invokeLater {
            editor.contentComponent.cursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR)
        }
        
        // Request AI completion asynchronously
        GlobalScope.launch {
            try {
                val aiService = ApplicationManager.getApplication().getService(AIService::class.java)
                val suggestions = aiService.getCompletions(
                    contextBefore = contextBefore,
                    contextAfter = contextAfter,
                    language = getLanguageFromEditor(editor),
                    project = project
                )
                
                ApplicationManager.getApplication().invokeLater {
                    // Reset cursor
                    editor.contentComponent.cursor = java.awt.Cursor.getDefaultCursor()
                    
                    if (suggestions.isNotEmpty()) {
                        // Show the first suggestion as inline ghost text
                        AIInlineRenderer.showInlineSuggestion(editor, suggestions.first())
                    } else {
                        Messages.showInfoMessage(
                            project,
                            "No AI suggestions available for the current context.",
                            "No Suggestions"
                        )
                    }
                }
            } catch (e: Exception) {
                ApplicationManager.getApplication().invokeLater {
                    editor.contentComponent.cursor = java.awt.Cursor.getDefaultCursor()
                    Messages.showErrorDialog(
                        project,
                        "Failed to get AI completion: ${e.message}",
                        "AI Completion Error"
                    )
                }
            }
        }
    }
    
    private fun getContextBefore(document: com.intellij.openapi.editor.Document, offset: Int): String {
        val start = maxOf(0, offset - 2000)
        return document.getText(com.intellij.openapi.util.TextRange(start, offset))
    }
    
    private fun getContextAfter(document: com.intellij.openapi.editor.Document, offset: Int): String {
        val end = minOf(document.textLength, offset + 500)
        return document.getText(com.intellij.openapi.util.TextRange(offset, end))
    }
    
    private fun getLanguageFromEditor(editor: Editor): String {
        val virtualFile = editor.virtualFile
        return virtualFile?.extension?.let { ext ->
            when (ext.lowercase()) {
                "java" -> "Java"
                "kt" -> "Kotlin"
                "py" -> "Python"
                "js" -> "JavaScript"
                "ts" -> "TypeScript"
                "cpp", "cc", "cxx" -> "C++"
                "c" -> "C"
                "cs" -> "C#"
                "go" -> "Go"
                "rs" -> "Rust"
                "php" -> "PHP"
                "rb" -> "Ruby"
                "swift" -> "Swift"
                "scala" -> "Scala"
                "html" -> "HTML"
                "css" -> "CSS"
                "xml" -> "XML"
                "json" -> "JSON"
                "yaml", "yml" -> "YAML"
                "sql" -> "SQL"
                "sh" -> "Shell"
                else -> "Text"
            }
        } ?: "Text"
    }
}