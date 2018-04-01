package com.conas.gradle.dependency

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConasDependencyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('conas', ConasDependency, project)
    }
}
