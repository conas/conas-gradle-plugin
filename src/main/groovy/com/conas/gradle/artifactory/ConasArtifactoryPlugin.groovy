package com.conas.gradle.artifactory

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.GenerateMavenPom

class ConasArtifactoryPlugin implements Plugin<Project> {

    private static final String SPRING_BOOT_PLUGIN = 'org.springframework.boot'

    @Override
    void apply(Project project) {
        project.extensions.create('conasArtifactory', ConasArtifactoryExtension)
        def releaseTask = project.task('release', type: ConasArtifactoryReleaseTask)
        def buildTask

        // In case Spring boot plugin is present lets use the
        if(project.plugins.hasPlugin(SPRING_BOOT_PLUGIN)) {
            buildTask = project.tasks.getByName('bootJar')
        } else {
            buildTask = project.tasks.getByName('build')
        }

        releaseTask.dependsOn(buildTask)

        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        if(project.plugins.hasPlugin(SPRING_BOOT_PLUGIN)) {
                            artifact project.tasks.bootJar
                        } else {
                            artifact project.tasks.jar
                        }

                        pom.withXml {
                            asNode().appendNode('description', 'Conas release')

                            final def dependenciesNode = asNode().appendNode('dependencies')

                            project.configurations.compile.allDependencies.each {
                                def dependencyNode = dependenciesNode.appendNode('dependency')
                                dependencyNode.appendNode('groupId', it.group)
                                dependencyNode.appendNode('artifactId', it.name)
                                dependencyNode.appendNode('version', it.version)
                            }
                        }
                    }
                }
            }
        }
    }
}