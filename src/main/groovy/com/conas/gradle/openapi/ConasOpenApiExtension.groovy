package com.conas.gradle.openapi


class ConasOpenApiExtension {
    public static final String EXTENSION_NAME = 'conasOpenApi';

    public boolean verbose = false;
    public String apiName = null
    public String apiPath = 'schema/api.yml'
    public String outputDir = 'schema/spec'
}
