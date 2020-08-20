package com.sri.gradle.tasks;

import java.io.File;

public interface OutputBuilder {
  /**
   * Sets work's output directory.
   *
   * @param outputDir the path to this work's output directory.
   */
  void intoDir(File outputDir);
}
