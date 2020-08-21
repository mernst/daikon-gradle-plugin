package com.sri.gradle.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Filefinder {
  private static final String JAVA  = "java";
  private static final String CLASS  = "class";
  private static final String JAR  = "jar";

  private static final PathMatcher  JAVA_MATCHER  = FileSystems.getDefault()
      .getPathMatcher("glob:*." + JAVA);
  private static final PathMatcher  CLASS_MATCHER  = FileSystems.getDefault()
      .getPathMatcher("glob:*." + CLASS);
  private static final PathMatcher  JAR_MATCHER  = FileSystems.getDefault()
      .getPathMatcher("glob:*." + JAR);

  private Filefinder(){}

  /**
   * List all Java files found in a directory. Skip those ones matching
   * the provide skip hints.
   *
   * @param directory directory to access
   * @param exclude hints for files to be excluded in the directory.
   * @return the list of files matching a given extension.
   */
  public static List<File> findJavaFiles(Path directory, String... exclude){
    if (!Files.exists(directory)) return Immutable.list();

    return findFiles(directory.toFile(), JAVA_MATCHER, JAVA, exclude);
  }

  /**
   * List all Java classes found in a directory. Skip those ones matching
   * the provide skip hints.
   *
   * @param directory directory to access
   * @param exclude hints for files to be excluded in the directory.
   * @return the list of files matching a given extension.
   */
  public static List<File> findJavaClasses(Path directory, String... exclude){
    if (!Files.exists(directory)) return Immutable.list();

    return findFiles(directory.toFile(), CLASS_MATCHER, CLASS, exclude);
  }

  /**
   * List all jar files found in a directory. Skip those ones matching
   * the provide skip hints. Avoids using classloaders.
   *
   * @param directory directory to access
   * @param exclude hints for files to be excluded in the directory.
   * @return the list of files matching a given extension.
   */
  public static List<File> findJavaJars(Path directory, String... exclude){
    if (!Files.exists(directory)) return Immutable.list();

    return findFiles(directory.toFile(), JAR_MATCHER, JAR, exclude);
  }

  /**
   * List all Java files found in a directory.
   * Skip those ones matching the provide skip hints.
   *
   * @param directory directory to access
   * @param matcher file matching strategy
   * @param ext file extension
   * @param skipHints keywords used to avoid certain files collection.
   * @return the list of files matching a given extension.
   */
  private static List<File> findFiles(File directory, final PathMatcher matcher, final String ext, String... skipHints){

    try {
      return Immutable.listOf(
          walkDirectory(directory, matcher, ext, skipHints)
      );
    } catch (IOException e) {
      System.err.printf("Error: unable to crawl %s. See %s%n", directory.getName(), e);
    }

    return Immutable.list();
  }


  /**
   * Crawls a directory structure in search of files matching an file extension. At
   * the same time it will skip those files that contains certain keywords.
   *
   * @param location the directory location
   * @param keywords skip hints
   * @return a list of interesting files
   * @throws IOException unexpected error has occurred.
   */
  private static List<File> walkDirectory(final File location, final PathMatcher matcher, final String ext, final String... keywords) throws IOException {

    final Path start = Paths.get(location.toURI());

    final List<File>  files   = new ArrayList<>();
    final Set<String> excluded = Immutable.setOf(Arrays.asList(keywords));

    try {
      Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

          final Path fileName = file.getFileName();

          if(matcher.matches(fileName)){
            final File visitedFile = file.toFile();
            final String name = visitedFile.getName().replace(("." + ext), "");

            if(!isExcluded(name, excluded)){
              files.add(visitedFile);
            }
          }

          return FileVisitResult.CONTINUE;
        }
      });

    } catch (IOException ignored) { }

    return files;

  }

  private static boolean isExcluded(String name, Set<String> excludedSet){
    if(excludedSet.isEmpty()) return false; // base case

    for(String each : excludedSet){
      if(name.contains(each)) return true;
    }

    return false;
  }

}
