package com.conas.gradle

import com.conas.gradle.dependency.ConasDependencyPlugin
import com.conas.gradle.idea.ConasIdeaPlugin
import com.conas.gradle.sources.ConasJavadocJarPlugin
import com.conas.gradle.sources.ConasSourcesJarPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ConasPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(ConasIdeaPlugin)
        project.plugins.apply(ConasDependencyPlugin)
        project.plugins.apply(ConasSourcesJarPlugin)
        project.plugins.apply(ConasJavadocJarPlugin)
    }
}
