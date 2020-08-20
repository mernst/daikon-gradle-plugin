package com.sri.gradle.tasks;

import com.sri.gradle.Constants;
import java.io.File;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class GenerateLikelyInvariants extends ToolTask {
  private final DirectoryProperty outputDir;
  private final RegularFileProperty daikonJar;
  private final Property<String> testsClassesPath;

  @SuppressWarnings("UnstableApiUsage")
  public GenerateLikelyInvariants(){
    this.daikonJar = getProject().getObjects().fileProperty();  // unchecked warning
    this.outputDir = getProject().getObjects().directoryProperty(); // unchecked warning
    this.testsClassesPath = getProject().getObjects().property(String.class); // unchecked warning
  }

  @TaskAction public void generateLikelyInvariants() {
    final WorkExecutorImpl executor = new WorkExecutorImpl();

    final File inputDir = getProject()
        .getLayout()
        .getBuildDirectory()
        .dir(getTestsClassesPath()).get().getAsFile();
    final File daikonJar = getDaikonJar()
        .getAsFile()
        .get();
    final File outputDir = getOutputDir()
        .getAsFile()
        .get();

    WorkConfiguration configuration = (ex -> ex
        .generateLikelyInvariants(inputDir)
        .includedSysClasspath(daikonJar)
        .intoDir(outputDir)
    );

    getLogger().quiet("Created task configuration");

    executor.install(configuration);
    getLogger().quiet("Configured GenerateLikelyInvariants task");

    getLogger().quiet("About to execute task");
    executor.execute();
    getLogger().quiet("Successfully executed task");
  }

  @OutputDirectory public DirectoryProperty getOutputDir() {
    return this.outputDir;
  }

  @Input public Property<String> getTestsClassesPath() {
    return this.testsClassesPath;
  }

  @InputFile public RegularFileProperty getDaikonJar() {
    return this.daikonJar;
  }

  @Override protected String getTaskName() {
    return Constants.TASK_GEN_LIKELY_INVARIANTS;
  }
}
