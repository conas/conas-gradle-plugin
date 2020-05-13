package com.conas.gradle.swagger

import com.conas.gradle.artifactory.ArtifactoryApiCaller
import okhttp3.MediaType
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

class OpenApiReleaseTask extends BaseOpenApiTask {

    private static final String API_FILE_NAME = 'swagger.json'

    @TaskAction
    releaseApi() {
        doRelease(extension())
    }

    static File getApiFile(Project project, ConasOpenApiExtension extension) {
        return new File(Paths.get(project.buildDir.absolutePath,
                                  extension.outputDir,
                                  API_FILE_NAME).toString())
    }

    private void doRelease(ConasOpenApiExtension extension) {
        final def artifactory = artifactoryModel(project, extension)

        ArtifactoryApiCaller.push(artifactory,
                                  releaseName(project, extension),
                                  getApiFile(project, extension),
                                  MediaType.parse('application/json'),
                                 true)
    }
}
