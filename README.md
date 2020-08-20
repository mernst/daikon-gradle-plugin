# Daikon Gradle Plugin
(experimental) daikon gradle plugin

This Gradle plug-in creates a task to run [Daikon](https://plse.cs.washington.edu/daikon/) on Java projects.

## Integration

To use this plug-in, [you must have downloaded Daikon and have it as dependency](https://plse.cs.washington.edu/daikon/download/).

Add the plug-in dependency and apply it in your project's `build.gradle`:
```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        ...
        classpath 'com.sri.gradle:daikon-gradle-plugin:1.0'
    }
}
```

## Applying the Plugin

### Java

```groovy
apply plugin: 'java'
apply plugin: 'com.sri.gradle.daikon'
```

## Daikon configuration

In the build.gradle of the project that applies the plugin:
```groovy
runDaikon {
    daikonJar = file("libs/daikon.jar")
    outputDir = file("${projectDir}/src/test/resources")
    testClassesPath = "path/to/"
}
```

## Task

* `generateLikelyInvariants` - runs Daikon workflow'.
