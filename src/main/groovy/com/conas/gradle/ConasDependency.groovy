package com.conas.gradle

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine
import org.gradle.api.Project
import org.yaml.snakeyaml.Yaml

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConasDependency {

    private final static String CONAS_DEPENDENCIES_PROPERTY = "conas.dependencies.version";
    private final static TemplateEngine TEMPLATE_ENGINE = new SimpleTemplateEngine()
    private final static Template CONAS_DEPENDENCIES_ARTIFACT =
            TEMPLATE_ENGINE.createTemplate('com.conas:conas-dependencies:${dependenciesVersion}@yml')

    private final AtomicBoolean configResolved = new AtomicBoolean(false);
    private final Lock configLock = new ReentrantLock()

    private final Project project;
    private DependencyStore dependencyStore;

    public String dependenciesVersion = null

    ConasDependency(Project project) {
        this.project = project;
    }

    private resolveConfig() {
        if(configResolved.get()) {
            return
        }
        configLock.lockInterruptibly()

        def dependenciesVersion =
                project.properties.get(CONAS_DEPENDENCIES_PROPERTY) ?: dependenciesVersion

        if(!dependenciesVersion) {
            throw new MissingPropertyException("Missing property \"conas.dependencies.version\"")
        }

        def artifact =
                CONAS_DEPENDENCIES_ARTIFACT
                        .make(dependenciesVersion: dependenciesVersion)
                        .toString()

        try {
            def rawConf =
                    project
                        .configurations
                        .detachedConfiguration(
                            project.dependencies.create(artifact))

            Map conf = rawConf.singleFile.withReader { reader ->
                return (Map) new Yaml().load(reader)
            }

            final def dependencies = conf['dependencies']
            final def templates = [:]

            for(dependency in dependencies) {
                templates.put(dependency.key, TEMPLATE_ENGINE.createTemplate((String) dependency.value))
            }

            dependencyStore = new DependencyStore(templates, conf)

        } finally {
            configLock.unlock()
        }
    }

    String dependency(String dependencyKey) {
        resolveConfig()

        return dependencyStore.getDependencyString(dependencyKey)
    }

    private class DependencyStore {

        private final Map<String, Template> templates;
        private final Map<String, ?> configuration;

        DependencyStore(Map<String, Template> templates, Map<String, ?> configuration) {
            this.templates = templates
            this.configuration = configuration
        }

        String getDependencyString(String dependencyKey) {
            def template = this.templates.get(dependencyKey)
            if(!template) {
                throw new NoSuchElementException("Missing dependency template for $dependencyKey");
            }
            return template.make(configuration).toString()
        }
    }
}
