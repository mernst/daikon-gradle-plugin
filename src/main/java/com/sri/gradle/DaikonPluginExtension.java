package com.sri.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

public class DaikonPluginExtension {

  private final DirectoryProperty outputdir;
  private final DirectoryProperty requires;
  private final Property<String> testdriverpack;


  @SuppressWarnings("UnstableApiUsage")
  public DaikonPluginExtension(Project project) {
    this.outputdir = project.getObjects().directoryProperty();
    this.requires = project.getObjects().directoryProperty();
    this.testdriverpack = project.getObjects().property(String.class);
  }

  public Property<String> getTestdriverpack() {
    return testdriverpack;
  }

  public DirectoryProperty getOutputdir() {
    return outputdir;
  }

  public DirectoryProperty getRequires() {
    return requires;
  }

}