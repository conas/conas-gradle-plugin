package com.conas.gradle.openapi

import com.conas.gradle.artifactory.ArtifactoryApi
import com.conas.gradle.artifactory.ArtifactoryHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class ConasOpenApiReleaseTask extends DefaultTask {

    @TaskAction
    releaseApi() {
        def openApiExtension =
                (ConasOpenApiExtension) project
                                            .extensions
                                            .getByName(ConasOpenApiExtension.EXTENSION_NAME)

        doRelease(openApiExtension)
    }

    private void doRelease(ConasOpenApiExtension extension) {
        def apiJson = new File(project.buildDir.absolutePath, extension.outputDir + '/swagger.json')
        def credentials = ArtifactoryHelper.artifactoryCredentials(project)

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl('http://35.204.110.148/artifactory/example-repo-local/')
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse('application/json'),
                        apiJson
                );

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", apiJson.getName(), requestFile)

        def service = retrofit.create(ArtifactoryApi);
        def call = service.pushArtifact('Basic ' + credentials.basicAuthorization(), extension.apiName, body)
        println('Basic ' + credentials.basicAuthorization())
        def response = call.execute()
        println(response.headers())
        println(response.code())
    }
}
