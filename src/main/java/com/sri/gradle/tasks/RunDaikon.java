package com.sri.gradle.tasks;

import com.sri.gradle.Options;
import com.sri.gradle.utils.Filefinder;
import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class RunDaikon extends AbstractNamedTask {
  private final DirectoryProperty outputdir;
  private final DirectoryProperty neededlibs;
  private final Property<String> driverpackage;

  @SuppressWarnings("UnstableApiUsage")
  public RunDaikon(){
    this.neededlibs = getProject().getObjects().directoryProperty();  // unchecked warning
    this.outputdir = getProject().getObjects().directoryProperty(); // unchecked warning
    this.driverpackage = getProject().getObjects().property(String.class); // unchecked warning
  }

  @TaskAction public void daikonRun() {
    final TaskExecutorImpl executor = new TaskExecutorImpl();

    final DirectoryProperty buildDir = getProject()
        .getLayout()
        .getBuildDirectory();

    final Directory buildMainDir = buildDir.dir(Options.PROJECT_MAIN_CLASS_DIR.value()).get();
    final Directory buildTestDir = buildDir.dir(Options.PROJECT_TEST_CLASS_DIR.value()).get();

    final String testpath = getDriverpackage().get().replaceAll("\\.", "/");
    final File inputDir = buildTestDir.dir(testpath).getAsFile();


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

    final RunDaikonConfiguration config = new RunDaikonConfiguration(inputDir, classpath, outputDir);
    getLogger().quiet("Created RunDaikon task configuration");

    executor.install(config);
    getLogger().quiet("Configured RunDaikon task");

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
    return Options.DAIKON_TASK.value();
  }


  static class RunDaikonConfiguration extends AbstractConfiguration {

    private final File inputDir;
    private final List<File> classpath;
    private final File outputDir;

    RunDaikonConfiguration(File inputDir, List<File> classpath, File outputDir){
      this.inputDir = inputDir;
      this.classpath = classpath;
      this.outputDir = outputDir;
    }

    @Override protected void configure() {
      runDaikonOn(inputDir)
          .withClasspath(classpath)
          .toDir(outputDir);
    }
  }

  static abstract class AbstractConfiguration implements TaskConfiguration {
    TaskExecutor executor;

    @Override public final synchronized void configure(TaskExecutor executor) {
      try {
        if (this.executor != null){
          throw new IllegalStateException("executor already available");
        }

        this.executor = Objects.requireNonNull(executor);
        configure();
      } finally {
        this.executor = null;
      }
    }

    protected abstract void configure();

    /**
     * @see TaskExecutor#runDaikonOn(File)
     */
    protected TaskBuilder runDaikonOn(File testClassesDir){
      return executor.runDaikonOn(testClassesDir);
    }

    /**
     * @see TaskExecutor#addError(Throwable)
     */
    protected void addError(Throwable t) {
      executor.addError(t);
    }
  }
}
