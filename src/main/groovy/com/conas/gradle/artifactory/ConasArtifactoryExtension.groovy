package com.conas.gradle.artifactory


class ConasArtifactoryExtension implements Artifactory {

    String url
    String repository
    String username
    String password
    boolean ignoreBuildArtifacts = false
}
