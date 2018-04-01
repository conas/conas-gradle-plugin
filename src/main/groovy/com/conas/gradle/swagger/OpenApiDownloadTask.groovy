package com.conas.gradle.swagger

import com.conas.gradle.artifactory.ArtifactoryApiCaller
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

class OpenApiDownloadTask extends BaseOpenApiTask {

    @TaskAction
    downloadRequirements() {
        doDownload( retrieveExtension(project))
    }

    private void doDownload(ConasOpenApiExtension extension) {
        final def artifactory = artifactoryModel(project, extension)
        final def outputPath = Paths.get(project.projectDir.absolutePath, extension.requirementsPath).toString()
        final def outputDir = new File(outputPath.toString())

        FileUtils.deleteDirectory(outputDir)
        outputDir.mkdir()

        extension.requirementsMap.each { String apiName, String apiVersion ->
            ArtifactoryApiCaller.pull(artifactory, releaseName(apiName, apiVersion), outputPath)
        }
    }
}
