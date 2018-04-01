package com.conas.gradle.swagger

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin


class ConasOpenApiPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create(ConasOpenApiExtension.EXTENSION_NAME, ConasOpenApiExtension)

        def generationTask = project.task('openApiGeneration', type: OpenApiGenerationTask)
        def releaseTask = project.task('openApiRelease', type: OpenApiReleaseTask)
        releaseTask.dependsOn(generationTask)

        project.task('openApiDownload', type: OpenApiDownloadTask)

        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        artifact (
                                OpenApiReleaseTask.getApiFile(project, BaseOpenApiTask.retrieveExtension(project))) {

                            extension 'json'
                            classifier 'api_schema'
                        }
                    }
                }
            }
        }
    }
}
