package com.conas.gradle.openapi

import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths


class ConasOpenApiTask extends DefaultTask {

    @TaskAction
    generateSwaggerDocuments() {
        def openApiExtension =
                (ConasOpenApiExtension) project
                                            .extensions
                                            .getByName(ConasOpenApiExtension.EXTENSION_NAME)

        if(!openApiExtension.apiName) {
            throw new RuntimeException('Missing api name');
        }

        generateSchema(project.projectDir.path, project.buildDir.path, openApiExtension)
    }

    protected static void generateSchema(String inputPath, String buildDir, ConasOpenApiExtension extension) {
        final def configurator = new CodegenConfigurator()

        def schemaFile = new File(inputPath ,extension.apiPath)
        if(!schemaFile.exists()) {
            throw new RuntimeException('Schema file not found');
        }

        def outputDir = new File(buildDir, extension.outputDir)
        configurator.setLang('swagger')
        configurator.setVerbose(extension.verbose)
        configurator.setInputSpec(schemaFile.absolutePath)
        configurator.setOutputDir(outputDir.path)

        final def clientOptInput = configurator.toClientOptInput()
        new DefaultGenerator().opts(clientOptInput).generate();
    }
}