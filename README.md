gradle-aggregate-javadocs-plugin
================================

In a multi-project setup containing one or many Java-based projects, Javadocs are only created for individual subprojects.
There are certain use cases that requires you merge Javadocs for all subprojects of your build. Creating a reusable library
that is partitioned into sub-functionality but shipped together is a typical example. This plugin adds a task to the root
 project of the build allowing to aggregate Javadocs across all subprojects.

## Usage

### Applying the Plugin

To include, add the following to your build.gradle

    buildscript {
        repositories { jcenter() }

        dependencies {
            classpath 'com.netflix.nebula:gradle-aggregate-javadocs-plugin:2.4.+'
        }
    }

    apply plugin: 'nebula-aggregate-javadocs'

### Aggregating Javadocs

To aggregate Javadocs across all subprojects, execute the task `aggregateJavadocs` available to the root project. The task
declares a task dependencies on the task `javadoc` provided by the Java plugin. The resulting Javadoc report is located
in the directory `project.buildDir/docs/javadoc`.