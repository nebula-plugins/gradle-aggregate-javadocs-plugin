package nebula.plugin.javadoc

import nebula.test.IntegrationSpec
import org.gradle.util.GFileUtils

class NebulaAggregateJavadocPluginFunctionalTest extends IntegrationSpec {
    def "can produce aggregate Javadocs"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}
"""
        settingsFile << """
include 'a', 'b', 'c'
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)
        File subprojectB = addSubproject('b')
        createJavaClass(subprojectB)
        File subprojectC = addSubproject('c')
        createJavaClass(subprojectC)

        when:
        runTasksSuccessfully(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
        new File(javadocsClassesDir, 'HelloWorldFromB.html').exists()
        new File(javadocsClassesDir, 'HelloWorldFromC.html').exists()
    }

    private String createJavaClass(File projectDir) {
        String projectName = projectDir.name.toUpperCase()
        File javaFile = new File(projectDir, "src/main/java/com/netflix/HelloWorldFrom${projectName}.java")
        String fileContent = """
package com.netflix;

public class HelloWorldFrom${projectName} {
    /**
     * Prints hello world message.
     *
     * @return Message
     */
    public String getMessage() {
        return "Hello World from $projectName";
    }
}
"""
        GFileUtils.writeFile(fileContent, javaFile)
    }
}
