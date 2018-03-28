package com.conas.gradle

import com.conas.gradle.sources.ConasJavadocJarPlugin
import com.conas.gradle.sources.ConasSourcesJarPlugin
import com.conas.gradle.openapi.ConasOpenApiPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin


class ConasPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(ArtifactoryPlugin)
        project.plugins.apply(ConasIdeaPlugin)
        project.plugins.apply(ConasDependencyPlugin)
        project.plugins.apply(ConasSourcesJarPlugin)
        project.plugins.apply(ConasJavadocJarPlugin)
    }
}
