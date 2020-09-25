package com.sri.gradle.internal;

import com.sri.gradle.Options;
import com.sri.gradle.utils.ImmutableStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Chicory extends AbstractTool {

  public Chicory() {
    super();
  }

  public Chicory setOutputDirectory(Path directory) {
    args(String.format("--output_dir=%s", directory));
    return this;
  }

  public Chicory setMainClass(String name){
    if(name == null || name.isEmpty())
      return this;

    args(name);
    return this;
  }

  @Override public Chicory setToolJar(File toolJar){
    return (Chicory) super.setToolJar(toolJar);
  }

  @Override public Chicory setClasspath(List<URL> classpathUrls) {
    return (Chicory) super.setClasspath(classpathUrls);
  }

  @Override public Chicory setWorkingDirectory(Path directory) {
    return (Chicory) super.setWorkingDirectory(directory);
  }

  public Chicory setComparabilityFile(Path directory, String filename) {
    final Path resolved = directory.resolve(filename);
    args(String.format("--comparability-file=%s", resolved));
    return this;
  }

  public Chicory selectedClasses(List<String> fullyQualifiedClassNames) {
    selectPatterns(fullyQualifiedClassNames);
    return this;
  }

  @Override public List<String> execute() throws ToolException {
    try {
      final String classPath = getClasspath().stream()
          .map(URL::toString)
          .collect(Collectors.joining(File.pathSeparator));

      List<String> output = getBuilder()
          .arguments("-classpath", classPath)
          .arguments(Options.CHICORY_MAIN_CLASS.value())
          .arguments(getArgs())
          .execute();

      List<String> err = ImmutableStream.listCopyOf(output.stream()
          .filter(Objects::nonNull)
          .filter(s -> s.startsWith("Error: Could not find or load main")));

      if (!err.isEmpty()) throw new ToolException(BAD_DAIKON_ERROR);

      return output;

    } catch (Exception e){
      throw new ToolException(BAD_DAIKON_ERROR, e);
    }
  }
}
