package com.conas.gradle.idea

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.plugins.ide.idea.IdeaPlugin

class ConasIdeaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(IdeaPlugin).with { IdeaPlugin ideaPlugin ->
            if(project.rootProject != project) {
                return
            }

            project.plugins.withType(JavaPlugin) { JavaPlugin javaPlugin ->
                JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
                ideaPlugin.model.project.jdkName = convention.sourceCompatibility
                ideaPlugin.model.project.languageLevel = convention.targetCompatibility
            }

            ideaPlugin.model.project.vcs = 'Git'
        }
    }
}
