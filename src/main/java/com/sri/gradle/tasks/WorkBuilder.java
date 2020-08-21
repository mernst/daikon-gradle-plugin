package com.sri.gradle.tasks;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public interface WorkBuilder extends OutputBuilder {
  // TODO(has) add more Daikon's options
  /**
   * Builds classpath in order to both generate tests
   * and execute tests. Client can provide additional files
   * if needed.
   *
   * Note: this method assumes that the working directory is
   * the same as the output directory provided at the previous
   * invocation of {@link OutputBuilder#intoDir(File)}. It will
   * fail if this output directory is null.
   *
   * @param files additional files
   * @return a reference to the task builder
   */
  default OutputBuilder includedSysClasspath(File... files){
    return includedSysClasspath(Arrays.asList(files));
  }

  /**
   * Builds classpath from a list of files
   *
   * @param files additional files
   * @return a reference to the task builder
   */
  OutputBuilder includedSysClasspath(List<File> files);
}
