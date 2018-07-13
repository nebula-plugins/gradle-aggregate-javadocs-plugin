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

    def "Can exclude multiple subprojects by name"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    exclude 'b', 'c'
}
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)
        File subprojectB = addSubproject('b')
        createJavaClass(subprojectB)
        File subprojectC = addSubproject('c')
        createJavaClass(subprojectC)
        File subprojectD = addSubproject('d')
        createJavaClass(subprojectD)

        when:
        runTasksSuccessfully(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
        !new File(javadocsClassesDir, 'HelloWorldFromB.html').exists()
        !new File(javadocsClassesDir, 'HelloWorldFromC.html').exists()
        new File(javadocsClassesDir, 'HelloWorldFromD.html').exists()
    }

    def "Unmatched excluded subprojects result in build failure"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    exclude 'c'
}
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)
        File subprojectB = addSubproject('b')
        createJavaClass(subprojectB)

        when:
        runTasksWithFailure(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        !new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
        !new File(javadocsClassesDir, 'HelloWorldFromB.html').exists()
    }

    def "Can include multiple subprojects by name"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    include 'b', 'c'
}
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
        !new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
        new File(javadocsClassesDir, 'HelloWorldFromB.html').exists()
        new File(javadocsClassesDir, 'HelloWorldFromC.html').exists()
    }

    def "Unmatched included subprojects result in build failure"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    include 'c'
}
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)
        File subprojectB = addSubproject('b')
        createJavaClass(subprojectB)

        when:
        runTasksWithFailure(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        !new File(javadocsClassesDir, 'HelloWorldFromC.html').exists()
    }

    def "Included subproject without JavaPlugin result in build failure"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    include 'a'
}
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)

        when:
        runTasksWithFailure(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        !new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
    }

    def "Defined includes and excludes results in build failure"() {
        given:
        buildFile << """
apply plugin: nebula.plugin.javadoc.NebulaAggregateJavadocPlugin

subprojects {
    apply plugin: 'java'
}

$NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME {
    include 'a'
    exclude 'b'
}
"""

        File subprojectA = addSubproject('a')
        createJavaClass(subprojectA)
        File subprojectB = addSubproject('b')
        createJavaClass(subprojectB)

        when:
        runTasksWithFailure(NebulaAggregateJavadocPlugin.AGGREGATE_JAVADOCS_TASK_NAME)

        then:
        File aggregateJavaDocDir = new File(projectDir, 'build/docs/javadoc')
        File javadocsClassesDir = new File(aggregateJavaDocDir, 'com/netflix')
        !new File(javadocsClassesDir, 'HelloWorldFromA.html').exists()
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
