package com.sri.gradle.internal;

import java.nio.file.Path;

public class DaikonExecSpec extends MainExecSpec {
  public void setStandardOutput(Path directory, String filename) {
    final Path resolved = directory.resolve(filename);
    args(String.format("%s %s", "-o", directory.relativize(resolved)));
  }

  public void setDtraceFile(Path directory, String filename) {
    final Path resolved = directory.resolve(filename);
    args(String.format("%s", directory.relativize(resolved)));
  }
}
