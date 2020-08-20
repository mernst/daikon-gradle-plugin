package com.sri.gradle.tasks;

import com.sri.gradle.utils.Urls;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class WorkBuilderImpl implements WorkBuilder {

  private final WorkExecutorImpl executor;

  private final Path testClassesDir;
  private Path outputDir;

  private final List<URL> classpathUrls;

  public WorkBuilderImpl(Path testClassesDir, WorkExecutorImpl executor){
    this.executor = executor;
    this.testClassesDir = testClassesDir;
    this.outputDir = null;
    this.classpathUrls = new LinkedList<>();
  }

  public Path getTestClassesDir(){
    return testClassesDir;
  }

  public Path getOutputDir(){
    return outputDir;
  }

  public List<URL> getClasspath(){
    return classpathUrls;
  }

  @Override public void intoDir(File outputDir) {
    if (outputDir == null){
      executor.addError(new NullPointerException("output directory is null"));
      return;
    }

    if (this.outputDir == null){
      this.outputDir = outputDir.toPath();
    }

    if (classpathUrls.isEmpty()){
      executor.addError(new IllegalArgumentException("classpath is empty"));
    }
  }

  @Override public OutputBuilder includedSysClasspath(File... files) {
    for (File each : files){
      if (each == null) continue;
      classpathUrls.add(Urls.toURL(each.getAbsolutePath()));
    }

    return this;
  }
}
