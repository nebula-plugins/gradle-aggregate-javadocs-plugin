package nebula.plugin.javadoc

import nebula.test.ProjectSpec
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc

class NebulaAggregateJavadocPluginTest extends ProjectSpec {
    def "apply plugin to single project build"() {
        when:
        project.apply plugin: NebulaAggregateJavadocPlugin

        then:
        !project.tasks.findByName(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)
    }

    def "apply plugin to multi-project build without Java subprojects"() {
        given:
        addSubproject('sample1')
        addSubproject('sample2')
        addSubproject('sample3')

        when:
        project.apply plugin: NebulaAggregateJavadocPlugin

        then:
        !project.tasks.findByName(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)
    }

    def "apply plugin to multi-project build with Java subprojects"() {
        given:
        Project sampleProject1 = addSubproject('sample1')
        sampleProject1.apply plugin: JavaPlugin
        Project sampleProject2 = addSubproject('sample2')
        sampleProject2.apply plugin: JavaPlugin
        Project sampleProject3 = addSubproject('sample3')
        sampleProject3.apply plugin: JavaPlugin

        when:
        project.apply plugin: NebulaAggregateJavadocPlugin

        then:
        Javadoc aggregateJavadocs = project.tasks.findByName(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)
        aggregateJavadocs.description == 'Aggregates Javadoc API documentation of all subprojects.'
        aggregateJavadocs.group == 'documentation'
        aggregateJavadocs.destinationDir == project.file("$project.buildDir/docs/javadoc")
        aggregateJavadocs.classpath.files == project.files(sampleProject1.javadoc.classpath, sampleProject2.javadoc.classpath, sampleProject3.javadoc.classpath).files
    }

    def "apply plugin to multi-project build with partial Java subprojects"() {
        given:
        Project sampleProject1 = addSubproject('sample1')
        sampleProject1.apply plugin: JavaPlugin
        addSubproject('sample2')
        Project sampleProject3 = addSubproject('sample3')
        sampleProject3.apply plugin: JavaPlugin

        when:
        project.apply plugin: NebulaAggregateJavadocPlugin

        then:
        Javadoc aggregateJavadocs = project.tasks.findByName(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)
        aggregateJavadocs.description == 'Aggregates Javadoc API documentation of all subprojects.'
        aggregateJavadocs.group == 'documentation'
        aggregateJavadocs.destinationDir == project.file("$project.buildDir/docs/javadoc")
        aggregateJavadocs.classpath.files == project.files(sampleProject1.javadoc.classpath, sampleProject3.javadoc.classpath).files
    }
}