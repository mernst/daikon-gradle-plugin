package com.sri.gradle.internal;

import com.google.common.collect.Lists;
import com.sri.gradle.Constants;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.process.JavaExecSpec;

public class MainExecutor {
  private final Project project;

  public MainExecutor(Project project){
    this.project = project;
  }

  public void execDaikon(String classNamePrefix, List<File> classPath, Path outputDir){
    execDaikon(new DaikonExecSpecAction(
        project,
        classNamePrefix,
        classPath,
        outputDir
    ));
  }

  public void execDaikon(Action<DaikonExecSpec> action){
    Objects.requireNonNull(action);
    final DaikonExecSpec spec = new DaikonExecSpec();
    action.execute(spec);
    execDaikon(spec);
  }

  public void execDaikon(DaikonExecSpec daikonSpec){
    Objects.requireNonNull(daikonSpec);
    final FileCollection fullClasspath = daikonSpec.getClasspath();
    project.javaexec(spec -> {
      spec.setStandardOutput(Constants.SILENT);
      spec.setWorkingDir(daikonSpec.getWorkingDir());
      spec.setClasspath(fullClasspath);
      spec.setMain(Constants.DAIKON_MAIN_CLASS);
      spec.setArgs(Arrays.asList(daikonSpec.getArgs()));
      daikonSpec.getConfigureFork().forEach(forkAction -> forkAction.execute(spec));
      project.getLogger().debug(getEscapedCmdLine(spec));
    });
  }

  public void execDynComp(List<File> classPath, List<String> allClassnames, String targetClass, Path testClassDir, Path outputDir){
    execDynComp(new DynCompExecSpecAction(
        project,
        classPath,
        allClassnames,
        targetClass,
        testClassDir,
        outputDir
    ));
  }

  public void execDynComp(Action<DynCompExecSpec> action){
    Objects.requireNonNull(action);
    final DynCompExecSpec spec = new DynCompExecSpec();
    action.execute(spec);
    execDynComp(spec);
  }

  public void execDynComp(DynCompExecSpec dynCompSpec){
    Objects.requireNonNull(dynCompSpec);
    final FileCollection fullClasspath = dynCompSpec.getClasspath();
    project.javaexec(spec -> {
      spec.setStandardOutput(Constants.SILENT);
      spec.setWorkingDir(dynCompSpec.getWorkingDir());
      spec.setClasspath(fullClasspath);
      spec.setMain(Constants.DYN_COMP_MAIN_CLASS);
      spec.setArgs(Arrays.asList(dynCompSpec.getArgs()));
      dynCompSpec.getConfigureFork().forEach(forkAction -> forkAction.execute(spec));
      project.getLogger().debug(getEscapedCmdLine(spec));
    });
  }

  public void execChicory(List<File> classPath, List<String> allClassnames, String targetClass, String classNamePrefix, Path outputDir){
    execChicory(new ChicoryExecSpecAction(
        project,
        classPath,
        allClassnames,
        targetClass,
        classNamePrefix,
        outputDir
    ));
  }

  public void execChicory(Action<ChicoryExecSpec> action){
    Objects.requireNonNull(action);
    final ChicoryExecSpec spec = new ChicoryExecSpec();
    action.execute(spec);
    execChicory(spec);
  }

  public void execChicory(ChicoryExecSpec chicorySpec){
    Objects.requireNonNull(chicorySpec);
    final FileCollection fullClasspath = chicorySpec.getClasspath();
    project.javaexec(spec -> {
      spec.setStandardOutput(Constants.SILENT);
      spec.setWorkingDir(chicorySpec.getWorkingDir());
      spec.setClasspath(fullClasspath);
      spec.setMain(Constants.CHICORY_MAIN_CLASS);
      spec.setArgs(Arrays.asList(chicorySpec.getArgs()));
      chicorySpec.getConfigureFork().forEach(forkAction -> forkAction.execute(spec));
      project.getLogger().debug(getEscapedCmdLine(spec));
    });
  }


  static String getEscapedCmdLine(JavaExecSpec spec){
    return new CmdLine(spec.getExecutable())
        .setClasspath(spec.getClasspath())
        .setMain(spec.getMain())
        .setArgs(spec.getArgs() == null ? Lists.newArrayList() : spec.getArgs())
        .build();
  }

  static class CmdLine {
    final String executable;
    String classpath;
    String main;
    String args;

    CmdLine(String executable){
      this.executable  = executable;
      this.classpath  = null;
      this.main  = null;
      this.args  = null;
    }

    CmdLine setClasspath(FileCollection cp){
      final Collection<String> escapedData = ObjEscaper.escapeIterable(cp);
      final Iterator<String> iterator = escapedData.iterator();

      final StringBuilder stringBuilder = new StringBuilder();
      if (iterator.hasNext()) {
        stringBuilder.append(iterator.next());

        while (iterator.hasNext()) {
          stringBuilder.append(Constants.PATH_SEPARATOR).append(iterator.next());
        }
      }

      this.classpath  = stringBuilder.toString();

      return this;
    }

    CmdLine setMain(String main){
      this.main = main;
      return this;
    }

    CmdLine setArgs(List<String> strings){
      List<String> data = Lists.newArrayList();

      for (String each : strings){
        data.add(ObjEscaper.escape(each));
      }

      this.args = String.join(" ", data);

      return this;
    }


    String build(){
      return this.executable
          + " " + "-cp " + this.classpath + " "
          + this.main + " "
          + this.args;
    }
  }

  static class ObjEscaper {

    static <T> Collection<String> escapeIterable(Iterable<T> data){
      final List<String> escapedData = new ArrayList<>();
      for (T t : data){
        escapedData.add(escape(t));
      }

      return escapedData;
    }

    static String escape(Object object){
      Object thatObject = object instanceof File ? new File(((File)object).toURI()) : object;
      return escape(thatObject.toString());
    }

    static String escape(String path) {
      return path.replace(" ", "\\ ");
    }
  }

  static class DynCompExecSpecAction implements Action<DynCompExecSpec> {

    private final Project project;
    private final List<File> classPath;
    private final List<String> allClassnames;
    private final String targetClass;
    private final Path testClassDir;
    private final Path outputDir;

    public DynCompExecSpecAction(Project project, List<File> classPath, List<String> allClassnames, String targetClass, Path testClassDir, Path outputDir){
      this.project = project;
      this.classPath = classPath;
      this.allClassnames = allClassnames;
      this.targetClass = targetClass;
      this.testClassDir = testClassDir;
      this.outputDir = outputDir;
    }

    @Override public void execute(DynCompExecSpec spec) {
      spec.setWorkingDir(testClassDir);
      spec.setOutputDirectory(testClassDir.relativize(outputDir));
      spec.setClasspath(project.files(classPath));
      spec.setMain(Constants.DYN_COMP_MAIN_CLASS);
      spec.setSelectedClasses(allClassnames);
      spec.setTargetClass(targetClass);
      spec.setForkOptions();
    }
  }

  static class ChicoryExecSpecAction implements Action<ChicoryExecSpec> {

    private final Project project;
    private final List<File> classPath;
    private final List<String> allClassnames;
    private final String targetClass;
    private final String classNamePrefix;
    private final Path outputDir;

    public ChicoryExecSpecAction(Project project, List<File> classPath, List<String> allClassnames, String targetClass, String classNamePrefix, Path outputDir){
      this.project = project;
      this.classPath = classPath;
      this.allClassnames = allClassnames;
      this.targetClass = targetClass;
      this.classNamePrefix = classNamePrefix;
      this.outputDir = outputDir;
    }

    @Override public void execute(ChicoryExecSpec spec) {
      spec.setWorkingDir(outputDir);
      spec.setClasspath(project.files(classPath));
      spec.setMain(Constants.CHICORY_MAIN_CLASS);
      spec.setComparabilityFile(outputDir, classNamePrefix + ".decls-DynComp");
      spec.setTargetClass(targetClass);
      spec.setSelectedClasses(allClassnames);
      spec.setForkOptions();
    }
  }

  static class DaikonExecSpecAction implements Action<DaikonExecSpec> {

    private final Project project;
    private final String classNamePrefix;
    private final List<File> classPath;
    private final Path outputDir;

    DaikonExecSpecAction(Project project, String classNamePrefix, List<File> classPath, Path outputDir){
      this.project = project;
      this.classNamePrefix = classNamePrefix;
      this.classPath = classPath;
      this.outputDir = outputDir;
    }

    @Override public void execute(DaikonExecSpec spec) {
      spec.setWorkingDir(outputDir);
      spec.setClasspath(project.files(classPath));
      spec.setMain(Constants.DAIKON_MAIN_CLASS);
      spec.setDtraceFile(outputDir, classNamePrefix + ".dtrace.gz");
      spec.setStandardOutput(outputDir, classNamePrefix + ".inv.gz");
      spec.setForkOptions();
    }
  }
}
