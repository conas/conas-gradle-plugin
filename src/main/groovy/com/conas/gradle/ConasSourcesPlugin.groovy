package com.conas.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.jvm.tasks.Jar


class ConasSourcesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def sourcesJar =
                project.task("sourcesJar", type: Jar) { task ->
                    def pluginConvention = project.convention.getPlugin(JavaPluginConvention)
                    task.classifier = 'sources'
                    task.from(pluginConvention.sourceSets.main.allSource)
                }
                
        project.artifacts { artifacts ->
            artifacts.add("archives", sourcesJar)
        }
    }
}
