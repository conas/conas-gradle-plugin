package com.conas.gradle.artifactory

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

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
                        }
                    }
                }
            }
        }

        initializeArtifactoryRepo(project)
    }

    private void initializeArtifactoryRepo(Project project) {
        final def artifactory = ArtifactoryHelper.artifactoryGlobal(project)

        try {
            ArtifactoryHelper.validate(artifactory)
        } catch (ArtifactoryException ignored) {
            return
        }

        project.repositories {
            maven {
                url artifactory.repositoryUrl()

                credentials {
                    username artifactory.username
                    password artifactory.username
                }
            }
        }
    }
}