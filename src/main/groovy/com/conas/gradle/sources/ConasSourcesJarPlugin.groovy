package com.conas.gradle.sources

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar

class ConasSourcesJarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def sourcesTask = project.task("sourcesJar", type: Jar) { Jar jar ->
            def pluginConvention = project.convention.getPlugin(JavaPluginConvention)
            // TODO: fix this
            jar.classifier = 'sources'
            jar.extension = 'jar'
            jar.from(pluginConvention.sourceSets.main.allSource)
        }

        project.artifacts {
            add("archives", sourcesTask)
        }

        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        artifact project.tasks.sourcesJar
                    }
                }
            }
        }
    }
}
