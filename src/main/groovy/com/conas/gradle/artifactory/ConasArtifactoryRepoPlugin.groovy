package com.conas.gradle.artifactory

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConasArtifactoryRepoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final def artifactory = ArtifactoryHelper.artifactoryGlobal(project)

        ArtifactoryHelper.validate(artifactory)

        project.repositories {
            maven {
                url artifactory.repositoryUrl()

                credentials {
                    username artifactory.username
                    password artifactory.password
                }
            }
        }
    }
}
