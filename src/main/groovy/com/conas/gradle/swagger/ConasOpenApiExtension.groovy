package com.conas.gradle.swagger

import com.conas.gradle.artifactory.ArtifactoryModel

class ConasOpenApiExtension {
    public static final String EXTENSION_NAME = 'conasOpenApi';

    public boolean verbose = false;
    public String apiName = null
    public String apiVersion = null
    public String apiPath = 'schema/api.yml'
    public String outputDir = 'schema/spec'
    public String requirementsPath = 'schema/requirements'
    public HashMap<String, String> requirementsMap = new HashMap<>()

    protected ArtifactoryModel artifactoryModel = new ArtifactoryModel()

    void artifactory(Closure closure) {
        this.artifactoryModel.configure(closure)
    }

    void requirements(Map<String, String> requirements) {
        requirementsMap << requirements
    }
}
