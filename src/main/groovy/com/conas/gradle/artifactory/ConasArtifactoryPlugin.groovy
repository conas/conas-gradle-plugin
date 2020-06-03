package com.conas.gradle.artifactory

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class ConasArtifactoryPlugin implements Plugin<Project> {

    private static final String SPRING_BOOT_PLUGIN = 'org.springframework.boot'

    @Override
    void apply(Project project) {
        project.extensions.create('conasArtifactory', ConasArtifactoryExtension)
        final def releaseTask = project.task('release', type: ConasArtifactoryReleaseTask)
        def buildTask

        // In case Spring boot plugin is present lets use the
        if(project.plugins.hasPlugin(SPRING_BOOT_PLUGIN)) {
            buildTask = project.tasks.getByName('bootJar')
        } else {
            buildTask = project.tasks.getByName('build')
        }

        releaseTask.dependsOn(buildTask)

        if (releaseTask.artifactoryExtension().ignoreBuildArtifacts) {
            println("Ignoring build artifacts")
            return
        }

        // Note: (all) rethink how to handle duplicates
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

                            addDependencies(dependenciesNode, extractSet(project, 'compile'))
                            addDependencies(dependenciesNode, extractSet(project, 'implementation'))
                        }
                    }
                }
            }
        }
    }

    private DependencySet extractSet(Project project, String property) {
        return project.configurations[property].allDependencies
    }

    private void addDependencies (rootNode, DependencySet dependencySet) {
        dependencySet.each {
            // Note: figure out why we receive null artifacts here
            if (it.group == null) {
                return
            }

            final def node = rootNode.appendNode('dependency')
            node.appendNode('groupId', it.group)
            node.appendNode('artifactId', it.name)
            node.appendNode('version', it.version)
        }
    }
}
