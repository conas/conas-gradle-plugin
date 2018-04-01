package com.conas.gradle.artifactory

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

import java.nio.file.Files
import java.nio.file.Paths

class ArtifactoryApiCaller {

    static void push(ArtifactoryModel artifactory,
                     String releaseName,
                     File file,
                     MediaType mediaType = null,
                     boolean verbose = true) {

        if(mediaType == null) {
            mediaType = MediaType.parse(Files.probeContentType(file.toPath()))
        }

        final def requestFile = RequestBody.create(mediaType, file);
        final def retrofit = retrofit(artifactory.repositoryUrl())
        final def api = retrofit.create(ArtifactoryApi);
        final def call = api.pushArtifact(authorization(artifactory), releaseName, requestFile)

        final def response = call.execute()

        if(response.code() != 201) {
            System.err.println("Error publishing to repository \"${artifactory.repository}\", " +
                               "statusCode: ${response.code()}")
            return
        }

        if(verbose) {
            println("Successfully published: ${releaseName}")
            println(response.body().string())
        }
    }

    static void pull(ArtifactoryModel artifactory, String releaseName, String outputLocation) {
        final def retrofit = retrofit(artifactory.repositoryUrl())
        final def api = retrofit.create(ArtifactoryApi);
        final def call = api.pullArtifact(authorization(artifactory), releaseName)
        final def response = call.execute()

        if(response.code() != 200) {
            System.err.println("Error pulling from repository \"${artifactory.repository}\", " +
                               "statusCode: ${response.code()}")
            return
        }

        final def outputFile = new File(Paths.get(outputLocation, releaseName).toString())
        Files.copy(response.body().byteStream(), outputFile.toPath())

        println("Succesfully downloaded api: ${releaseName}")
    }

    private static String authorization(ArtifactoryModel artifactory) {
        return "Basic ${artifactory.basicAuthorization()}"
    }

    private static Retrofit retrofit(String url) {
        return new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
    }
}
