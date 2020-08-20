package com.sri.gradle.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Urls {

  public static URL toURL(String filepath) throws RuntimeException {
    final URL source;
    try {
      source = createUrlFrom(filepath);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not create source path!", e);
    }
    return source;
  }

  private static URL createUrlFrom(final String path) throws MalformedURLException {
    return new File(path).toURI().toURL();
  }
}
