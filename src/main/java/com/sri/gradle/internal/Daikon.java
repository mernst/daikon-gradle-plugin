package com.sri.gradle.internal;

import com.sri.gradle.Options;
import com.sri.gradle.utils.ImmutableStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Daikon extends AbstractTool {
  public Daikon() {
    super();
  }

  @Override public Daikon setToolJar(File toolJar){
    return (Daikon) super.setToolJar(toolJar);
  }

  public Daikon setStandardOutput(String filename) {
    args(String.format("-o %s", filename));

    return this;
  }

  @Override public Daikon setClasspath(List<URL> classpathUrls) {
    return (Daikon) super.setClasspath(classpathUrls);
  }

  @Override public Daikon setWorkingDirectory(Path directory) {
    return (Daikon) super.setWorkingDirectory(directory);
  }

  public Daikon setDtraceFile(Path directory, String filename) {
    final Path resolved = directory.resolve(filename);
    args(String.format("%s", resolved));
    return this;
  }

  @Override public List<String> execute() throws ToolException {
    try {
      final String classPath = getClasspath().stream()
          .map(URL::toString)
          .collect(Collectors.joining(File.pathSeparator));

      List<String> output = getBuilder()
          .arguments("-classpath", classPath)
          .arguments(Options.DAIKON_MAIN_CLASS.value())
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
