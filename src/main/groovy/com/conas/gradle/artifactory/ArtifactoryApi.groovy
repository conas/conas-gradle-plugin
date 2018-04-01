package com.conas.gradle.artifactory

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ArtifactoryApi {

    @PUT("{artifact}")
    Call<ResponseBody> pushArtifact(@Header("Authorization") String authorization,
                                    @Path("artifact") String artifact,
                                    @Body RequestBody body);

    @Streaming
    @GET("{artifact}")
    Call<ResponseBody> pullArtifact(@Header("Authorization") String authorization,
                                    @Path("artifact") String artifact);
}
