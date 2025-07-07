package org.jetbrains.plugins.template.completion

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class AcceptSuggestionAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        
        // Try to accept current inline suggestion
        if (!AIInlineRenderer.acceptCurrentSuggestion(editor)) {
            // If no AI suggestion is available, perform default tab behavior
            performDefaultTabAction(editor)
        }
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.getData(CommonDataKeys.PROJECT)
        
        // Enable action only when editor is available and focused
        e.presentation.isEnabled = editor != null && project != null && editor.contentComponent.hasFocus()
    }
    
    private fun performDefaultTabAction(editor: Editor) {
        // If there's a selection, replace it with tab or indentation
        if (editor.selectionModel.hasSelection()) {
            editor.selectionModel.removeSelection()
        } else {
            // Insert tab or perform indentation
            val caretOffset = editor.caretModel.offset
            val document = editor.document
            val tabSize = editor.settings.getTabSize(editor.project)
            val indentString = " ".repeat(tabSize)
            
            document.insertString(caretOffset, indentString)
            editor.caretModel.moveToOffset(caretOffset + indentString.length)
        }
    }
}
