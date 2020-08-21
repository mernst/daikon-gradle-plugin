package com.sri.gradle.tasks;

import com.sri.gradle.Constants;
import com.sri.gradle.utils.Filefinder;
import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class GenerateLikelyInvariants extends ToolTask {
  private final DirectoryProperty outputdir;
  private final DirectoryProperty neededlibs;
  private final Property<String> driverpackage;

  @SuppressWarnings("UnstableApiUsage")
  public GenerateLikelyInvariants(){
    this.neededlibs = getProject().getObjects().directoryProperty();  // unchecked warning
    this.outputdir = getProject().getObjects().directoryProperty(); // unchecked warning
    this.driverpackage = getProject().getObjects().property(String.class); // unchecked warning
  }

  @TaskAction public void generateLikelyInvariants() {
    final WorkExecutorImpl executor = new WorkExecutorImpl();

    final DirectoryProperty buildDir = getProject()
        .getLayout()
        .getBuildDirectory();

    final Directory buildMainDir = buildDir.dir(Constants.PATH_TO_BUILD_MAIN_DIR).get();
    final Directory buildTestDir = buildDir.dir(Constants.PATH_TO_BUILD_TEST_DIR).get();

    final String testpath = getDriverpackage().get().replaceAll("\\.", "/");
    final File inputDir = buildTestDir.dir(testpath).getAsFile();

    if (!Files.exists(inputDir.toPath())){
      throw new GradleException("compiled test classes not available");
    }

    final File dependenciesDir = getNeededlibs()
        .getAsFile()
        .get();

    final List<File> classpath = new LinkedList<>(Filefinder.findJavaJars(dependenciesDir.toPath()));
    classpath.add(buildMainDir.getAsFile());
    classpath.add(buildTestDir.getAsFile());


    final File outputDir = getOutputdir()
        .getAsFile()
        .get();

    if (!Files.exists(outputDir.toPath())){
      if (!outputDir.mkdir()){
        throw new GradleException("Unable to create output directory");
      }
    }

    WorkConfiguration configuration = (ex -> ex
        .generateLikelyInvariants(inputDir)
        .includedSysClasspath(classpath)
        .intoDir(outputDir)
    );

    getLogger().quiet("Created task configuration");

    executor.install(configuration);
    getLogger().quiet("Configured GenerateLikelyInvariants task");

    getLogger().quiet("About to execute task");
    executor.execute();
    getLogger().quiet("Successfully executed task");
  }

  @OutputDirectory public DirectoryProperty getOutputdir() {
    return this.outputdir;
  }

  @Input public Property<String> getDriverpackage() {
    return this.driverpackage;
  }

  @InputDirectory public DirectoryProperty getNeededlibs() {
    return this.neededlibs;
  }

  @Override protected String getTaskName() {
    return Constants.TASK_GEN_LIKELY_INVARIANTS;
  }
}
