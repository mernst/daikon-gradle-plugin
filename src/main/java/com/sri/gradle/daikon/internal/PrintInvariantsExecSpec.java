package com.sri.gradle.daikon.internal;

import java.nio.file.Path;

public class PrintInvariantsExecSpec extends MainExecSpec {
  public void setInvariantsFile(Path directory, String filename){
    args(String.format("%s", relativizeFile(directory, filename)));
  }

  public void setOutputFile(Path directory, String filename) {
    args("--output", String.format("%s", relativizeFile(directory, filename)));
  }

  public void setWrapXml(){
    args("--wrap_xml");
  }

  public void setOutputNumberSamples(){
    args("--output_num_samples");
  }
}
