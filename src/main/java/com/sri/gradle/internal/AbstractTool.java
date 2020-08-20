package com.sri.gradle.internal;

import static java.util.Arrays.stream;

import com.sri.gradle.utils.Command;
import com.sri.gradle.utils.Urls;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractTool {
  protected static final String BAD_DAIKON_ERROR = "Unable to run Daikon. Are you sure daikon.jar is in your path?";
  private final Command.Builder builder;

  private final List<URL> classpathUrls;
  private Object[] args = new Object[0];

  public AbstractTool(){
    this.builder = Command.create()
        .arguments("java")
        .arguments("-Xmx4G")
        .permitNonZeroExitStatus();

    this.classpathUrls = new LinkedList<>();
  }

  public Object[] getArgs() {
    return args;
  }

  protected void args(Object... args) {
    this.args = Stream.concat(
        stream(this.args), stream(args)).toArray(Object[]::new);
  }

  public AbstractTool setToolJar(File toolJar){
    getClasspath().add(Urls.toURL(toolJar.getAbsolutePath()));
    return this;
  }

  public AbstractTool setClasspath(List<URL> classpathUrls){
    this.classpathUrls.clear();
    this.classpathUrls.addAll(classpathUrls);
    return this;
  }

  public List<URL> getClasspath(){
    return classpathUrls;
  }

  public abstract List<String> execute() throws ToolException;

  public Command.Builder getBuilder(){
    return builder;
  }

  public AbstractTool selectPatterns(List<String> fullyQualifiedClassNamePatterns){
    //noinspection Convert2streamapi
    for(String qualifiedName : fullyQualifiedClassNamePatterns){ // unchecked warning
      selectPattern(qualifiedName);
    }

    return this;
  }

  public AbstractTool selectPattern(String classnamePattern){
    args("--ppt-select-pattern=" + classnamePattern);
    return this;
  }

  public AbstractTool help(){
    args("--help");
    return this;
  }

  public AbstractTool setWorkingDirectory(Path directory) {
    builder.workingDirectory(directory.toFile());
    return this;
  }

  public AbstractTool omitPatterns(List<String> fullyQualifiedClassNamePatterns){
    //noinspection Convert2streamapi
    for(String qualifiedName : fullyQualifiedClassNamePatterns){ // unchecked warning
      omitPattern(qualifiedName);
    }

    return this;
  }

  public AbstractTool omitPattern(String classnamePattern){
    args("--ppt-omit-pattern=" + classnamePattern);
    return this;
  }


}
