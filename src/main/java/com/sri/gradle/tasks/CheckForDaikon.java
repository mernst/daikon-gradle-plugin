package com.sri.gradle.tasks;

import com.sri.gradle.Options;
import com.sri.gradle.internal.Daikon;
import java.io.File;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class CheckForDaikon extends AbstractNamedTask {

  @TaskAction public void checkForDaikon(){
    try {
      final File daikonJar = getJarfile(
          Options.DAIKON_JAR_FILE
      );

      new Daikon()
          .setToolJar(daikonJar)
          .help()
          .execute();

    } catch (Exception e){
      throw new GradleException(UNEXPECTED_ERROR);
    }


  }

  @Override protected String getTaskName() {
    return "checkForDaikon";
  }
}
