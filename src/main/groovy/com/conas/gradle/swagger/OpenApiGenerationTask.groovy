package com.conas.gradle.swagger

import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.tasks.TaskAction

class OpenApiGenerationTask extends BaseOpenApiTask {

    @TaskAction
    generateSwaggerDocuments() {
        generateSchema(project.projectDir.path, project.buildDir.path, extension())
    }

    protected static void generateSchema(String inputPath, String buildDir,
                                         ConasOpenApiExtension extension) {

        final def configurator = new CodegenConfigurator()

        final def schemaFile = new File(inputPath ,extension.apiPath)
        if(!schemaFile.exists()) {
            throw new OpenApiException('Missing openApi schema file');
        }

        final def outputDir = new File(buildDir, extension.outputDir)
        configurator.setLang('swagger')
        configurator.setVerbose(extension.verbose)
        configurator.setInputSpec(schemaFile.absolutePath)
        configurator.setOutputDir(outputDir.path)

        final def clientOptInput = configurator.toClientOptInput()
        new DefaultGenerator().opts(clientOptInput).generate();
    }
}