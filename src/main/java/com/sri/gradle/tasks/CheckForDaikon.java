package com.sri.gradle.tasks;

import com.sri.gradle.Constants;
import com.sri.gradle.internal.Daikon;
import java.io.File;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class CheckForDaikon extends NamedTask {

  @TaskAction public void checkForDaikon(){
    try {
      final File daikonJar = getProject().getLayout()
          .getProjectDirectory()
          .dir(Constants.LIB_DIR)
          .file(Constants.DAIKON_JAR)
          .getAsFile();

      new Daikon()
          .setToolJar(daikonJar)
          .help()
          .execute();

    } catch (Exception e){
      throw new GradleException(
          "Daikon is not installed on this machine.\n" +
          "For latest release, see: https://github.com/codespecs/daikon/releases"
      );
    }


  }

  @Override protected String getTaskName() {
    return "daikon";
  }
}
