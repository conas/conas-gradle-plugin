package com.conas.gradle.artifactory

import org.gradle.util.ConfigureUtil

class ArtifactoryModel implements Artifactory {

    String url
    String repository
    String username
    String password

    ArtifactoryModel() {}
    ArtifactoryModel(String url, String repository, String username, String password) {
        this.url = url
        this.repository = repository
        this.username = username
        this.password = password
    }

    void configure(Closure closure) {
        ConfigureUtil.configure(closure, this)
    }

    String repositoryUrl() {
        return "${url}${repository}/"
    }

    String basicAuthorization() {
        def basic = this.username + ':' + this.password
        byte[] encoded = Base64.encoder.encode(basic.getBytes())
        return new String(encoded)
    }
}
