package com.sri.gradle.internal;

import com.sri.gradle.Constants;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DynComp extends AbstractTool {
  public DynComp(){
    super();
  }

  public DynComp setOutputDirectory(Path directory) {
    args(String.format("--output_dir=%s", directory));
    return this;
  }

  public DynComp setMainClass(String name){
    if(name == null || name.isEmpty())
      return this;

    args(name);
    return this;
  }

  @Override public DynComp setWorkingDirectory(Path directory) {
    return (DynComp) super.setWorkingDirectory(directory);
  }

  @Override public DynComp setToolJar(File toolJar){
    return (DynComp) super.setToolJar(toolJar);
  }

  @Override public DynComp setClasspath(List<URL> classpathUrls) {
    return (DynComp) super.setClasspath(classpathUrls);
  }

  public DynComp selectedClasses(List<String> fullyQualifiedClassNames) {
    selectPatterns(fullyQualifiedClassNames);
    return this;
  }

  public DynComp omittedClasses(List<String> fullyQualifiedClassNames) {
    omitPatterns(fullyQualifiedClassNames);
    return this;
  }

  @Override public List<String> execute() throws ToolException {
    try {
      final String classPath = getClasspath().stream()
          .map(URL::toString)
          .collect(Collectors.joining(File.pathSeparator));

      return getBuilder()
          .arguments("-classpath", classPath)
          .arguments(Constants.DYN_COMP_MAIN)
          .arguments(getArgs())
          .execute();

    } catch (Exception e){
      throw new ToolException(BAD_DAIKON_ERROR, e);
    }
  }
}
