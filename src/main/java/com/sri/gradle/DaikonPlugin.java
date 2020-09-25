package com.sri.gradle;

import com.sri.gradle.tasks.AbstractNamedTask;
import com.sri.gradle.tasks.CheckForDaikon;
import com.sri.gradle.tasks.RunDaikon;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class DaikonPlugin implements Plugin<Project> {

  @Override public void apply(Project project) {
    DaikonPluginExtension extension = project.getExtensions().create(
        Options.PLUGIN_EXTENSION.value(), DaikonPluginExtension.class, project);

    final CheckForDaikon checkDaikonInstallation = createCheckForDaikon(project);

    final RunDaikon mainTask = createRunDaikonTask(project, extension);
    mainTask.dependsOn(checkDaikonInstallation);
    getCheckedAndCarryOn(project, mainTask);

    project.getLogger().quiet("Executing " + mainTask.getName());
  }

  private static void getCheckedAndCarryOn(Project project, Task task) {
    project.getTasksByName(Options.CHECK_TASK.value(), false).forEach(it -> it.dependsOn(task));
  }

  private RunDaikon createRunDaikonTask(Project project, DaikonPluginExtension extension) {
    final RunDaikon mainTask = project.getTasks().create(Options.DAIKON_TASK.value(), RunDaikon.class);
    mainTask.setGroup(Options.GROUP.value());
    mainTask.setDescription(Options.PLUGIN_DESCRIPTION.value());
    mainTask.dependsOn(Options.ASSEMBLE_TASK.value());

    mainTask.getRequires().set(extension.getRequires());
    mainTask.getOutputdir().set(extension.getOutputdir());
    mainTask.getTestdriverpack().set(extension.getTestdriverpack());

    return mainTask;
  }

  private CheckForDaikon createCheckForDaikon(Project project){
    // Chicory and DynComp can be accessed thru daikon.jar;
    // meaning if daikon.jar is there we can assume they are there too
    return createCheckTask(project, Options.CHECK_DAIKON_TASK.value(), CheckForDaikon.class);
  }

  @SuppressWarnings("SameParameterValue")
  private static <T extends AbstractNamedTask> T createCheckTask(Project project, String taskName, Class<T> taskClass){
    final T checkTask = project.getTasks().create(taskName, taskClass);
    checkTask.setGroup(Options.GROUP.value());
    return checkTask;
  }

}