gradle-aggregate-javadocs-plugin
================================
[![Build Status](https://travis-ci.org/nebula-plugins/gradle-aggregate-javadocs-plugin.svg?branch=master)](https://travis-ci.org/nebula-plugins/gradle-aggregate-javadocs-plugin)
[![Coverage Status](https://coveralls.io/repos/nebula-plugins/gradle-aggregate-javadocs-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/nebula-plugins/gradle-aggregate-javadocs-plugin?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nebula-plugins/gradle-aggregate-javadocs-plugin?utm_source=badgeutm_medium=badgeutm_campaign=pr-badge)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-aggregate-javadocs-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)


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
            classpath 'com.netflix.nebula:gradle-aggregate-javadocs-plugin:2.2.+'
        }
    }

    apply plugin: 'nebula-aggregate-javadocs'

### Aggregating Javadocs

To aggregate Javadocs across all subprojects, execute the task `aggregateJavadocs` available to the root project. The task
declares a task dependencies on the task `javadoc` provided by the Java plugin. The resulting Javadoc report is located
in the directory `project.buildDir/docs/javadoc`.