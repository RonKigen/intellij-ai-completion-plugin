package org.jetbrains.plugins.template.completion

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class AIConfigurable : SearchableConfigurable {

    private val settings = AISettings.getInstance()
    private lateinit var panel: DialogPanel

    override fun createComponent(): JComponent {
        panel = panel {
            group("AI Code Completion Settings") {
                row {
                    checkBox("Enable AI code completion")
                        .bindSelected(settings::isEnabled)
                }

                row("API Key:") {
                    passwordField()
                        .bindText(settings::apiKey)
                        .columns(40)
                        .comment("Enter your Google AI Studio API key")
                }

                row("API Endpoint:") {
                    textField()
                        .bindText(settings::apiEndpoint)
                        .columns(40)
                }

                row("Model:") {
                    textField()
                        .bindText(settings::model)
                        .columns(30)
                }

                row("Max Tokens:") {
                    intTextField()
                        .bindIntText(settings::maxTokens)
                        .columns(10)
                }

                row("Temperature:") {
                    textField()
                        .text(settings.temperature.toString())
                        .columns(10)
                        .comment("0.0 = deterministic, 1.0 = creative")
                }

                separator()

                row {
                    checkBox("Auto-trigger completions")
                        .bindSelected(settings::autoTrigger)
                }

                row("Trigger delay (ms):") {
                    intTextField()
                        .bindIntText(settings::triggerDelay)
                        .columns(10)
                }

                row("Min prefix length:") {
                    intTextField()
                        .bindIntText(settings::minPrefixLength)
                        .columns(10)
                }
            }
        }

        return panel
    }

    override fun isModified(): Boolean = panel.isModified()
    override fun apply() = panel.apply()
    override fun reset() = panel.reset()
    override fun getDisplayName(): String = "AI Code Completion"
    override fun getId(): String = "ai.codecompletion"
}
