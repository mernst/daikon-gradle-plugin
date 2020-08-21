package com.sri.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public class DaikonPluginExtension {

  private final DirectoryProperty outputdir;
  private final DirectoryProperty neededlibs;
  private final Property<String> driverpackage;


  @SuppressWarnings("UnstableApiUsage")
  public DaikonPluginExtension(Project project) {
    this.outputdir = project.getObjects().directoryProperty();
    this.neededlibs = project.getObjects().directoryProperty();
    this.driverpackage = project.getObjects().property(String.class);
  }

  public Property<String> getDriverpackage() {
    return driverpackage;
  }

  public DirectoryProperty getOutputdir() {
    return outputdir;
  }

  public DirectoryProperty getNeededlibs() {
    return neededlibs;
  }

}