package com.sri.gradle.tasks;

import com.google.common.collect.ImmutableSet;
import com.sri.gradle.Options;
import java.io.File;
import java.util.Set;
import javax.annotation.Nonnull;
import org.gradle.api.DefaultTask;

public abstract class AbstractNamedTask extends DefaultTask {
  // TODO(has) pls consider pruning this set
  private static final Set<Options> ALLOWED_SET = ImmutableSet.of(
      Options.DAIKON_JAR_FILE,
      Options.CHICORY_JAR_FILE,
      Options.DYN_COMP_RT_JAR_FILE,
      Options.DYN_COMP_PRE_MAIN_JAR_FILE
  );

  protected static final String UNEXPECTED_ERROR = "Daikon is not installed on this machine.\n" +
      "For latest release, see: https://github.com/codespecs/daikon/releases";

  protected abstract String getTaskName();

  @SuppressWarnings("SameParameterValue")
  protected File getJarfile(Options jarName){
    if (!ALLOWED_SET.contains(jarName)){
      throw new IllegalArgumentException("Unknown options");
    }

    return getProject().getLayout()
        .getProjectDirectory()
        .dir(Options.PROJECT_LIB_DIR.value())
        .file(jarName.value())
        .getAsFile();
  }

  @Override @Nonnull public String toString() {
    return getTaskName();
  }
}
