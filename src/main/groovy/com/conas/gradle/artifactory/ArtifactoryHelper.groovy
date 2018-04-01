package com.conas.gradle.artifactory

import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.Project

class ArtifactoryHelper {

    private static final String BASE = 'conas.artifactory'
    public static final String ARTIFACTORY_URL = "${BASE}.url"
    public static final String ARTIFACTORY_REPOSITORY = "${BASE}.repository"
    public static final String ARTIFACTORY_USERNAME = "${BASE}.username"
    public static final String ARTIFACTORY_PASSWORD = "${BASE}.password"

    static ArtifactoryModel artifactoryGlobal(Project project) {
        String url = (String) project.properties.get(ARTIFACTORY_URL)
        String repository = (String) project.properties.get(ARTIFACTORY_REPOSITORY)
        String username = (String) project.properties.get(ARTIFACTORY_USERNAME)
        String password = (String) project.properties.get(ARTIFACTORY_PASSWORD)

        return new ArtifactoryModel(url, repository, username, password)
    }

    static ArtifactoryModel merge(Artifactory m1, Artifactory m2) {
        return (ArtifactoryModel) merge(new ArtifactoryModel(), { key, value -> value != null  }, m1, m2)
    }

    static <T> T merge(T target, Closure closure, T... sources) {
        for(def source in sources) {
            source.properties.each { key, value ->
                if((key as String) in ["metaClass","class"]) {
                    return
                }
                final MetaClass metaClass = InvokerHelper.getMetaClass(target)

                if(metaClass.hasProperty(target, key as String)) {
                    if (closure(key, value)) {
                        metaClass.setProperty(target, key as String, value)
                    }
                }
            }
        }
        return target
    }

    static void validate(ArtifactoryModel model) {
       propsEach(model) { key, value ->
           if (value == null) {
               throw new ArtifactoryException("Missing property \"${key}\"")
           }
       }
    }

    static <T>void propsEach(T obj, Closure closure) {
        obj.properties.each { key, value ->
            if(closure.maximumNumberOfParameters != 2) {
                return
            }
            if((key as String) in ["metaClass","class"]) {
                return
            }
            closure(key, value)
        }
    }
}
