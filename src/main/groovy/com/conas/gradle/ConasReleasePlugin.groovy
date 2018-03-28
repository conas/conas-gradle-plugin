package com.conas.gradle

import com.conas.gradle.artifactory.ArtifactoryHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention


class ConasReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        artifact project.tasks.jar
                    }
                }
            }
        }

        def credentials = ArtifactoryHelper.artifactoryCredentials(project)

        ((ArtifactoryPluginConvention) project.convention.plugins.artifactory).with {
            publish {
                contextUrl = 'http://35.204.110.148/artifactory'
                repository {
                    repoKey = "test-repository"
                    username = credentials.username
                    password = credentials.password
                }
                defaults {
                    publications ('conas')
                }
            }
        }
    }
}
