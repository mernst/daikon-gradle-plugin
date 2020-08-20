package com.sri.gradle;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import com.sri.gradle.utils.Classfinder;
import com.sri.gradle.utils.Command;
import com.sri.gradle.utils.Javafinder;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.Test;

public class DaikonPluginTest {
  @Test public void testClassfinder(){
    List<Class<?>> files = Classfinder.findClasses(
        "com.sri.gradle", getClass().getClassLoader());

    assertFalse(files.isEmpty());
  }

  @Test public void testJavafinder(){
    Path dir = new File("src/main/java/com/sri/gradle/utils").toPath();
    System.out.println(dir);
    List<File> filesAvailable = Javafinder.findJavaFiles(dir);
    assertThat(filesAvailable.size(), is(5));
  }

  @Test public void testCommandBuilder(){
    List<String> filesAvailable = Command.create().arguments("ls")
        .permitNonZeroExitStatus().execute();

    assertThat(filesAvailable.size(), is(9));
  }
}