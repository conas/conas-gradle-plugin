package com.conas.gradle.openapi

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin


class ConasOpenApiPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create(ConasOpenApiExtension.EXTENSION_NAME, ConasOpenApiExtension)
        project.task('openApi', type: ConasOpenApiTask)
        project.task('openApiRelease', type: ConasOpenApiReleaseTask)

        project.plugins.withType(MavenPublishPlugin) {
            project.publishing {
                publications {
                    conas(MavenPublication) {
                        def openApiExtension =
                                (ConasOpenApiExtension) project
                                        .extensions
                                        .getByName(ConasOpenApiExtension.EXTENSION_NAME)

                        artifact new File(project.buildDir.absolutePath, openApiExtension.outputDir + '/swagger.json')
                    }
                }
            }
        }
    }
}
