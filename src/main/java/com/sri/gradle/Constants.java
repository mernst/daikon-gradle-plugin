package com.sri.gradle;

public class Constants {
  public static final String GROUP = "Daikon";
  public static final String EXTENSION_DAIKON_PLUGIN_NAME = "runDaikon";
  public static final String DAIKON_PLUGIN_DESCRIPTION = "Discovery likely invariants using Daikon.";
  public static final String MAIN = "daikon.Daikon";
  public static final String CHICORY_MAIN = "daikon.Chicory";
  public static final String DYN_COMP_MAIN = "daikon.DynComp";
  public static final String DAIKON_JAR = "daikon.jar";
  public static final String LIB_DIR = "libs";
  public static final String PATH_TO_BUILD_CLASSES = "classes/";
  public static final String PATH_TO_BUILD_TEST_DIR = PATH_TO_BUILD_CLASSES + "java/test";
  public static final String PATH_TO_BUILD_MAIN_DIR = PATH_TO_BUILD_CLASSES + "java/main";
  public static final String TASK_GEN_LIKELY_INVARIANTS = "generateLikelyInvariants";
  public static final String TASK_CHECK_FOR_DAIKON = "daikonCheck";
  public static final String TASK_CHECK = "check";
}
