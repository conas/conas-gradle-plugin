package com.conas.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConasPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(ConasIdeaPlugin)
        project.plugins.apply(ConasDependencyPlugin)
        project.plugins.apply(ConasReleasePlugin)
    }
}
