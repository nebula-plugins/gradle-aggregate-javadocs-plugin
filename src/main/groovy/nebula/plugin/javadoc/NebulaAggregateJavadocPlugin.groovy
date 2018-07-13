package nebula.plugin.javadoc

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc

class NebulaAggregateJavadocPlugin implements Plugin<Project> {
    static final String AGGREGATE_JAVADOCS_TASK_NAME = 'aggregateJavadocs'

    @Override
    void apply(Project project) {
        def extension = project.extensions.create(AGGREGATE_JAVADOCS_TASK_NAME, NebulaAggregateJavadocPluginExtension)
        Project rootProject = project.rootProject
        rootProject.gradle.projectsEvaluated {
            validateExtension(extension)
            Set<Project> javaSubprojects = getJavaSubprojects(rootProject, extension)
            if (!javaSubprojects.isEmpty()) {
                rootProject.task(AGGREGATE_JAVADOCS_TASK_NAME, type: Javadoc) {
                    description = 'Aggregates Javadoc API documentation of all subprojects.'
                    group = JavaBasePlugin.DOCUMENTATION_GROUP
                    dependsOn javaSubprojects.javadoc

                    source javaSubprojects.javadoc.source
                    destinationDir rootProject.file("$rootProject.buildDir/docs/javadoc")
                    classpath = rootProject.files(javaSubprojects.javadoc.classpath)
                }
            }
        }
    }

    private static Set<Project> getJavaSubprojects(Project rootProject, NebulaAggregateJavadocPluginExtension extension) {
        if (extension.include != null) {
            // We have a set of projects the user wants to include. Make sure each has the Java plugin and proceed
            def includes = new LinkedHashSet(Arrays.asList(extension.include))
            def projects = rootProject.subprojects.findAll { subproject -> includes.remove(subproject.name) }
            projects.forEach { subproject ->
                if (!subproject.plugins.hasPlugin(JavaPlugin)) {
                    throw new GradleException("$AGGREGATE_JAVADOCS_TASK_NAME-included project '$subproject.name' does not have the Java plugin, so Javadocs cannot be created" )
                }
            }

            // It is easy to rename a project and forget to add it to the includes. Accordingly, throw if not all includes
            // matched with an actual project
            if (!includes.empty) {
                throw new GradleException("No projects found matching $AGGREGATE_JAVADOCS_TASK_NAME includes: " + includes)
            }

            return projects
        } else {

            // By default use all projects with the JavaPlugin, but if excludes are defined, apply those
            def projects = rootProject.subprojects.findAll { subproject -> subproject.plugins.hasPlugin(JavaPlugin) }
            if (extension.exclude != null) {
                def excludes = new LinkedHashSet(Arrays.asList(extension.exclude))
                projects.removeIf { subproject -> excludes.remove(subproject.name) }

                // It is easy to rename a project and forget to add it to the excludes. Accordingly, throw if not all excludes
                // matched with an actual project
                if (!excludes.empty) {
                    throw new GradleException("No projects found matching $AGGREGATE_JAVADOCS_TASK_NAME excludes: " + excludes)
                }
            }

            return projects
        }
    }

    private static void validateExtension(NebulaAggregateJavadocPluginExtension extension) {
        if (extension.include != null && extension.exclude != null) {
            throw new GradleException("If defining includes, excludes cannot also be defined")
        }
    }
}