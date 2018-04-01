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

        if(project.plugins.hasPlugin(SPRING_BOOT_PLUGIN)) {
            def bootJar = project.tasks.getByName('bootJar')
            releaseTask.dependsOn(bootJar)

            project.plugins.withType(MavenPublishPlugin) {
                project.publishing {
                    publications {
                        conas(MavenPublication) {
                            artifact project.tasks.bootJar
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
                url artifactory.url

                credentials {
                    username artifactory.username
                    password artifactory.username
                }
            }
        }
    }
}