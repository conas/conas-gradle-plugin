package com.conas.gradle.artifactory

import org.gradle.api.Project
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class ArtifactoryHelper {

    public static final String ARTIFACTORY_USERNAME = 'conas.artifactory.username'
    public static final String ARTIFACTORY_PASSWORD = 'conas.artifactory.password'

    static ArtifactoryCredentials artifactoryCredentials(Project project) {
        String username = (String) project.properties.get(ARTIFACTORY_USERNAME)
        String password = (String) project.properties.get(ARTIFACTORY_PASSWORD)

        if(!username) {
            throw new MissingPropertyException("Missing property \"$ARTIFACTORY_USERNAME\"")
        }
        if(!password) {
            throw new MissingPropertyException("Missing property \"$ARTIFACTORY_PASSWORD\"")
        }

        return new ArtifactoryCredentials(username, password)
    }

    static ArtifactoryApi artifactoryApi(project) {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl('http://35.204.110.148/artifactory/example-repo-local/')
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

        return retrofit.create(ArtifactoryApi);
    }

    static class ArtifactoryCredentials {
        public String username;
        public String password;

        ArtifactoryCredentials(String username, String password) {
            this.username = username
            this.password = password
        }

        String basicAuthorization() {
            def basic = username + ':' + password
            def encoded = Base64.encoder.encode(basic.getBytes())
            return new String(encoded)
        }
    }
}
