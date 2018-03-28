package com.conas.gradle.artifactory

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ArtifactoryApi {

    @Multipart
    @PUT("{artifact}")
    Call<ResponseBody> pushArtifact(@Header("Authorization") String authorization,
                                    @Path("artifact") String artifact,
                                    @Part MultipartBody.Part file);

    @GET("{artifact}")
    Call<ResponseBody> pullArtifact(@Header("Authorization") String authorization,
                                    @Path("artifact") String artifact);
}
