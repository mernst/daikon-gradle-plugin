package com.sri.gradle.tasks;

import javax.annotation.Nonnull;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;

public abstract class ToolTask extends DefaultTask {
  protected abstract String getTaskName();

  @Override @Nonnull public String toString() {
    return getTaskName();
  }
}
