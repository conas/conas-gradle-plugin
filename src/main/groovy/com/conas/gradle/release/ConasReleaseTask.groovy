package com.conas.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention


class ConasReleaseTask extends DefaultTask {

    public static final String TASK_NAME = 'releaseTask'
    public static final String ARTIFACTORY_USERNAME = 'conas.artifactory.username'
    public static final String ARTIFACTORY_PASSWORD = 'conas.artifactory.password'

    @TaskAction
    setupArtifactory() {
        def artifactoryUsername = project.properties.get(ARTIFACTORY_USERNAME)
        def artifactoryPassword = project.properties.get(ARTIFACTORY_PASSWORD)

        if(!artifactoryUsername) {
            throw new MissingPropertyException("Missing property \"$ARTIFACTORY_USERNAME\"")
        }
        if(!artifactoryPassword) {
            throw new MissingPropertyException("Missing property \"$ARTIFACTORY_PASSWORD\"")
        }

        ((ArtifactoryPluginConvention) project.convention.plugins.artifactory).with {
            publish {
                contextUrl = 'http://35.204.110.148/artifactory'
                repository {
                    repoKey = "example-repo-local"
                    username = artifactoryUsername
                    password = artifactoryPassword
                }
                defaults {
                    publications ('conas')
                }
            }
        }
    }
}
