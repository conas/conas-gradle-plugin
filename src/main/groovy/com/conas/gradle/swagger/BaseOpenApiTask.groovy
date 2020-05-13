package com.conas.gradle.swagger

import com.conas.gradle.artifactory.ArtifactoryHelper
import com.conas.gradle.artifactory.ArtifactoryModel
import org.gradle.api.DefaultTask
import org.gradle.api.Project

abstract class BaseOpenApiTask extends DefaultTask {

    ConasOpenApiExtension extension() {
        def openApiExtension = retrieveExtension(project)

        if(!openApiExtension.apiName) {
            throw new OpenApiException('Missing property "apiName"');
        }

        return openApiExtension
    }

    static ConasOpenApiExtension retrieveExtension(Project project) {
        return (ConasOpenApiExtension) project
                .extensions
                .getByName(ConasOpenApiExtension.EXTENSION_NAME)
    }

    static ArtifactoryModel artifactoryModel(Project project, ConasOpenApiExtension extension) {
        def artifactory =
                ArtifactoryHelper.merge(ArtifactoryHelper.artifactoryGlobal(project),
                                        extension.artifactoryModel)

        ArtifactoryHelper.validate(artifactory)

        return artifactory
    }

    static String releaseName(String apiName, String apiVersion) {
        return "${apiName}-${apiVersion}.json"
    }

    static String releaseName(Project project, ConasOpenApiExtension extension) {
        return releaseName(extension.apiName, (extension.apiVersion ?: project.version) as String)
    }
}
