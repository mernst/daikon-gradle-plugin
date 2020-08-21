package com.sri.gradle;

import static com.sri.gradle.Constants.DAIKON_PLUGIN_DESCRIPTION;
import static com.sri.gradle.Constants.EXTENSION_DAIKON_PLUGIN_NAME;
import static com.sri.gradle.Constants.GROUP;
import static com.sri.gradle.Constants.TASK_CHECK_FOR_DAIKON;
import static com.sri.gradle.Constants.TASK_RUN_DAIKON;

import com.sri.gradle.tasks.CheckForDaikon;
import com.sri.gradle.tasks.RunDaikon;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class DaikonPlugin implements Plugin<Project> {

  @Override public void apply(Project project) {
    DaikonPluginExtension extension = project.getExtensions().create(
        EXTENSION_DAIKON_PLUGIN_NAME, DaikonPluginExtension.class, project);

    final CheckForDaikon checkDaikonInstallation = createCheckForDaikon(project);
    final RunDaikon mainTask = createRunDaikonTask(project, extension);
    mainTask.dependsOn(checkDaikonInstallation);
    makeTaskRunWithCheck(project, mainTask);

    project.getLogger().quiet("Executing " + mainTask.getName());
  }

  private static void makeTaskRunWithCheck(Project project, Task task) {
    project.getTasksByName(Constants.TASK_CHECK, false).forEach(it -> it.dependsOn(task));
  }

  private RunDaikon createRunDaikonTask(Project project, DaikonPluginExtension extension) {
    final RunDaikon mainTask = project.getTasks().create(TASK_RUN_DAIKON, RunDaikon.class);
    mainTask.setGroup(GROUP);
    mainTask.setDescription(DAIKON_PLUGIN_DESCRIPTION);
    mainTask.dependsOn("assemble");

    mainTask.getNeededlibs().set(extension.getNeededlibs());
    mainTask.getOutputdir().set(extension.getOutputdir());
    mainTask.getDriverpackage().set(extension.getDriverpackage());

    return mainTask;
  }

  private CheckForDaikon createCheckForDaikon(Project project){
    final CheckForDaikon checkForDaikon = project
        .getTasks()
        .create(TASK_CHECK_FOR_DAIKON, CheckForDaikon.class);
    checkForDaikon.setGroup(GROUP);
    return checkForDaikon;
  }

}