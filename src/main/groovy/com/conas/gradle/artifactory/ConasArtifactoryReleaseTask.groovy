package com.conas.gradle.artifactory

import okhttp3.MediaType
import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.TaskAction

class ConasArtifactoryReleaseTask extends DefaultTask {

    @TaskAction
    artifactoryRelease() {
        doRelease(artifactoryExtension(), publishingExtension())
    }

    ConasArtifactoryExtension artifactoryExtension() {
        return (ConasArtifactoryExtension) project.extensions.getByType(ConasArtifactoryExtension)
    }

    private PublishingExtension publishingExtension() {
        return (PublishingExtension) project.extensions.getByType(PublishingExtension)
    }

    private String releaseName(String fileName) {
        return "${projectGroup()}/${project.name}/${project.version}/${fileName}"
    }

    private String pomReleaseName() {
        return releaseName("${project.name}-${project.version}.pom")
    }

    private String projectGroup() {
        return (project.group as String).split('\\.').join('/')
    }

    private void doRelease(ConasArtifactoryExtension artifactoryExtension,
                           PublishingExtension publishingExtension) {

        final def artifactory = ArtifactoryHelper.merge(ArtifactoryHelper.artifactoryGlobal(project),
                                                        artifactoryExtension)

        ArtifactoryHelper.validate(artifactory)

        for(def publication in publishingExtension.publications.asList()) {
            // Note: (all) rethink this part
            def generatePomTask = project.tasks.getByName("generatePomFileForConasPublication") as GenerateMavenPom
            if(generatePomTask) {
                generatePomTask.doGenerate()
            }

            final def mavenPublication = (publication as DefaultMavenPublication)
            final def normalizedPublication = mavenPublication.asNormalisedPublication()

            final def pomFile = normalizedPublication.getPomArtifact().getFile()
            if(pomFile.exists()) {
                    ArtifactoryApiCaller.push(artifactory,
                                              pomReleaseName(),
                                              pomFile,
                                              resolveMediaType('pom'))
            }

            for(def artifact in mavenPublication.artifacts) {
                if(artifact.file.isDirectory()) {
                    throw new ArtifactoryException('Cannot publish a directory')
                }

                ArtifactoryApiCaller.push(artifactory,
                                          releaseName(artifact.file.name),
                                          artifact.file,
                                          resolveMediaType(artifact.extension))
            }
        }
    }

    private static MediaType resolveMediaType(String extension) {
        def mediaType
        switch (extension) {
            case 'jar':
                mediaType = 'application/java-archive'
                break;
            case 'pom':
                mediaType = 'application/pom'
                break;
            case 'json':
                mediaType = 'application/json'
                break;
            case 'yml':
                mediaType = 'application/x-yaml'
                break;
        }

        if(mediaType == null) {
            throw new RuntimeException('Unknown media type')
        }

        return MediaType.parse(mediaType)
    }
}
