package com.conas.gradle.sources

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar

class ConasJavadocJarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def javadocTask = project.task('javadocJar', type: Jar) { Jar jar ->
            def javadoc = (Javadoc) project.tasks.getByName('javadoc')
            jar.dependsOn(javadoc)
            // TODO: fix this
            jar.classifier = 'javadoc'
            jar.extension = 'jar'
            jar.from(javadoc.destinationDir)
        }

        project.artifacts {
            add("archives", javadocTask)
        }

        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        artifact project.tasks.javadocJar
                    }
                }
            }
        }
    }
}
