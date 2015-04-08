package nebula.plugin.javadoc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc

class NebulaAggregateJavadocPlugin implements Plugin<Project> {
    static final String AGGREGATE_JAVADOCS_TASK_NAME = 'aggregateJavadocs'

    @Override
    void apply(Project project) {
        Project rootProject = project.rootProject
        rootProject.gradle.projectsEvaluated {
            Set<Project> javaSubprojects = getJavaSubprojects(rootProject)
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

    private Set<Project> getJavaSubprojects(Project rootProject) {
        rootProject.subprojects.findAll { subproject -> subproject.plugins.hasPlugin(JavaPlugin) }
    }
}