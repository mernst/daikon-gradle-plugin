# Daikon Gradle Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

This Gradle plug-in creates a task, `runDaikon`, that runs [Daikon](https://plse.cs.washington.edu/daikon/) on Java projects' unit tests.

## Configuration

To use this plug-in, you must specify the location where the plugin can find Daikon.

In the following example, Daikon is in a project's `libs` directory: 

```groovy
runDaikon {
    // the project directory where daikon.jar, ChicoryPremain.jar,
    // and dcomp_*.jar files exist  
    requires = file("libs")
}
```

Also, you should specify both the Daikon output directory and the test driver package.

You can find an example of a complete configuration below:

```groovy
plugins {
    id 'java'
    id 'maven-publish'
    id 'com.sri.gradle.daikon' version '0.0.1-SNAPSHOT'
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url 'https://plugins.gradle.org/m2/'
    }
}

dependencies {
    implementation 'com.google.guava:guava:28.0-jre'
    testImplementation 'org.hamcrest:hamcrest:2.2'
    testImplementation 'junit:junit:4.13'
}
```

```groovy
runDaikon {
    outputDir = file("${projectDir}/build/daikon-output")
    // the project directory where daikon.jar, ChicoryPremain.jar,
    // and dcomp_*.jar files exist  
    requires = file("libs")
    // *TestDriver package name
    // If you use Randoop, then Randoop will generate this TestDriver for you.
    testDriverPackage = "com.foo"
    // However, if you are not using Randoop, then you need a TestDriver.
    // This TestDriver should have a static void main method, so Daikon can
    // executed. Having said that, this plugin will automatically generate
    // this TestDriver for you.
}
```

## Using a locally-built plugin

You can build the plugin locally rather than downloading it from Maven Central.

To build the plugin from source, run `./gradlew build`.

If you want to use a locally-built version of the plugin, you can publish the plugin to your
local Maven repository by running `./gradlew publishToMavenLocal`. Then, add the following to
the `settings.gradle` file in the Gradle project that you want to use the plugin:

```
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

## Daikon Tasks

The plugin support the following tasks. The main task of this plugin is the `runDaikon` task, which
runs other supporting tasks, such as `generateTestDriverCode`. The entire list of tasks is presented here:

- `daikonCheck` - Checks if Daikon is in your project's classpath.
- `generateTestDriverCode` - Generates test driver code that Daikon can execute.
- `runDaikon` - Detection of likely program invariants using Daikon.

Additional build properties:

-   `-Pdriver` - Tells the plugin to build its own test driver at `build/driver` directory.

## License

    Copyright (C) 2020 SRI International

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
