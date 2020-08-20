package com.sri.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public class DaikonPluginExtension {

  private final DirectoryProperty outputDir;
  private final RegularFileProperty daikonJar;
  private final Property<String> testClassesPath;


  @SuppressWarnings("UnstableApiUsage")
  public DaikonPluginExtension(Project project) {
    this.outputDir = project.getObjects().directoryProperty();
    this.daikonJar = project.getObjects().fileProperty();
    this.testClassesPath = project.getObjects().property(String.class);
  }

  public Property<String> getTestClassesPath() {
    return testClassesPath;
  }

  public DirectoryProperty getOutputDir() {
    return outputDir;
  }

  public RegularFileProperty getDaikonJar() {
    return daikonJar;
  }

}