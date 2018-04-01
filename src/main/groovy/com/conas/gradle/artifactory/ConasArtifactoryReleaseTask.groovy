package com.conas.gradle.artifactory

import okhttp3.MediaType
import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
import org.gradle.api.tasks.TaskAction

class ConasArtifactoryReleaseTask extends DefaultTask {

    @TaskAction
    artifactoryRelease() {
        doRelease(artifactoryExtension(), publishingExtension())
    }

    private ConasArtifactoryExtension artifactoryExtension() {
        return (ConasArtifactoryExtension) project.extensions.getByType(ConasArtifactoryExtension)
    }

    private PublishingExtension publishingExtension() {
        return (PublishingExtension) project.extensions.getByType(PublishingExtension)
    }

    private String releaseName(String fileName) {
        return "${project.group}/${project.version}/${fileName}"
    }

    private void doRelease(ConasArtifactoryExtension artifactoryExtension,
                           PublishingExtension publishingExtension) {

        final def artifactory = ArtifactoryHelper.merge(ArtifactoryHelper.artifactoryGlobal(project),
                                                        artifactoryExtension)

        ArtifactoryHelper.validate(artifactory)

        for(publication in publishingExtension.publications.asList()) {
            for(artifact in ((DefaultMavenPublication) publication).artifacts) {
                if(artifact.file.isDirectory()) {
                    throw new RuntimeException('Cannot publish a directory')
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
            case 'json':
                mediaType = 'application/json'
                break;
        }

        if(mediaType == null) {
            throw new RuntimeException('Unknown media type')
        }

        return MediaType.parse(mediaType)
    }
}
