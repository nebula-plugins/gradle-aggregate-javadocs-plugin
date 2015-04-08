package nebula.plugin.javadoc

import nebula.test.ProjectSpec
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.util.GFileUtils

class NebulaAggregateJavadocPluginIntegrationTest extends ProjectSpec {
    def "can produce aggregate Javadocs"() {
        given:
        Project sampleProject1 = addSubprojectWithDirectory('sample1')
        sampleProject1.apply plugin: JavaPlugin
        File sample1JavaFile = project.file("$sampleProject1.name/src/main/java/com/netflix/HelloWorld.java")
        String sample1JavaFileContent = """
package com.netflix;

public class HelloWorld {
    /**
     * Prints hello world message.
     *
     * @return Message
     */
    public String getMessage() {
        return "Hello World";
    }
}
"""
        GFileUtils.writeFile(sample1JavaFileContent, sample1JavaFile)

        Project sampleProject2 = addSubprojectWithDirectory('sample2')
        sampleProject2.apply plugin: JavaPlugin
        File sample2JavaFile = project.file("$sampleProject1.name/src/main/java/com/netflix/MyApp.java")
        String sample2JavaFileContent = """
package com.netflix;

public class MyApp {
    /**
     * Main entry point for my application.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {}
}
"""
        GFileUtils.writeFile(sample2JavaFileContent, sample2JavaFile)

        Project sampleProject3 = addSubprojectWithDirectory('sample3')
        sampleProject3.apply plugin: JavaPlugin
        File sample3JavaFile = project.file("$sampleProject1.name/src/main/java/com/netflix/Utils.java")
        String sample3JavaFileContent = """
package com.netflix;

public class Utils {
    /**
     * Does something with provided String.
     *
     * @param str Provided String
     * @return Transformed String
     */
    public String doSomething(String str) {
        return str;
    }
}
"""
        GFileUtils.writeFile(sample3JavaFileContent, sample3JavaFile)

        when:
        project.apply plugin: NebulaAggregateJavadocPlugin
        project.gradle.buildListenerBroadcaster.projectsEvaluated(project.gradle)
        Javadoc aggregateJavadocs = project.tasks.findByName(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)
        aggregateJavadocs.execute()

        then:
        aggregateJavadocs.destinationDir.exists()
        File javadocsClassesDir = new File(aggregateJavadocs.destinationDir, 'com/netflix')
        new File(javadocsClassesDir, 'HelloWorld.html').exists()
        new File(javadocsClassesDir, 'MyApp.html').exists()
        new File(javadocsClassesDir, 'Utils.html').exists()
    }
}