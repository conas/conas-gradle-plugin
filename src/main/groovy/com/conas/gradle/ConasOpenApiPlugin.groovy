package com.conas.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


class ConasOpenApiPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('conasApi', ConasApi)
    }
}
