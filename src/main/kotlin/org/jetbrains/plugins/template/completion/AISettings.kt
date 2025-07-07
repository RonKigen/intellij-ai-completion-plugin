package org.jetbrains.plugins.template.completion

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

@Service
@State(name = "AICodeCompletionSettings", storages = [Storage("ai-completion.xml")])
class AISettings : PersistentStateComponent<AISettings.State> {
    
    data class State(
        var isEnabled: Boolean = true,
        var apiKey: String = "",
        var apiEndpoint: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent",
        var model: String = "gemini-1.5-flash",
        var maxTokens: Int = 100,
        var temperature: Double = 0.2,
        var autoTrigger: Boolean = true,
        var triggerDelay: Int = 300,
        var minPrefixLength: Int = 2
    )
    
    private var state = State()
    
    override fun getState(): State = state
    override fun loadState(state: State) {
        this.state = state
    }
    
    companion object {
        fun getInstance(): AISettings {
            return ApplicationManager.getApplication().getService(AISettings::class.java)
        }
    }
    
    var isEnabled: Boolean
        get() = state.isEnabled
        set(value) { state.isEnabled = value }
    
    var apiKey: String
        get() = state.apiKey
        set(value) { state.apiKey = value }
    
    var apiEndpoint: String
        get() = state.apiEndpoint
        set(value) { state.apiEndpoint = value }
    
    var model: String
        get() = state.model
        set(value) { state.model = value }
    
    var maxTokens: Int
        get() = state.maxTokens
        set(value) { state.maxTokens = value }
    
    var temperature: Double
        get() = state.temperature
        set(value) { state.temperature = value }
    
    var autoTrigger: Boolean
        get() = state.autoTrigger
        set(value) { state.autoTrigger = value }
    
    var triggerDelay: Int
        get() = state.triggerDelay
        set(value) { state.triggerDelay = value }
    
    var minPrefixLength: Int
        get() = state.minPrefixLength
        set(value) { state.minPrefixLength = value }
}
