package org.jetbrains.plugins.template.completion

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object AIIcons {
    @JvmField
    val COPILOT: Icon = IconLoader.getIcon("/icons/copilot.svg", AIIcons::class.java)
    
    @JvmField
    val SUGGESTION: Icon = IconLoader.getIcon("/icons/suggestion.svg", AIIcons::class.java)
    
    @JvmField
    val AI: Icon = IconLoader.getIcon("/icons/ai.svg", AIIcons::class.java)
    
    @JvmField
    val GEMINI: Icon = IconLoader.getIcon("/icons/gemini.svg", AIIcons::class.java)
    
    @JvmField
    val LOADING: Icon = IconLoader.getIcon("/icons/loading.svg", AIIcons::class.java)
    
    // Fallback icons if custom icons are not available
    @JvmField
    val FALLBACK_COPILOT: Icon = IconLoader.getIcon("/general/add.svg", AIIcons::class.java)
    
    @JvmField
    val FALLBACK_SUGGESTION: Icon = IconLoader.getIcon("/general/inspectionsOK.svg", AIIcons::class.java)
    
    @JvmField
    val FALLBACK_AI: Icon = IconLoader.getIcon("/general/gear.svg", AIIcons::class.java)
}