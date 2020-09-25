package com.sri.gradle;

public enum Options {
  GROUP("Daikon"),
  PLUGIN_EXTENSION("runDaikon"),
  PLUGIN_DESCRIPTION("Discovery of likely invariants using Daikon."),
  DAIKON_MAIN_CLASS("daikon.Daikon"),
  CHICORY_MAIN_CLASS("daikon.Chicory"),
  DYN_COMP_MAIN_CLASS("daikon.DynComp"),
  DAIKON_JAR_FILE("daikon.jar"),
  CHICORY_JAR_FILE("ChicoryPremain.jar"),
  DYN_COMP_PRE_MAIN_JAR_FILE("dcomp_premain.jar"),
  DYN_COMP_RT_JAR_FILE("dcomp_rt.jar"),
  PROJECT_LIB_DIR("libs"),
  PROJECT_MAIN_CLASS_DIR("classes/java/main"),
  PROJECT_TEST_CLASS_DIR("classes/java/test"),
  DAIKON_TASK("daikonRun"),
  ASSEMBLE_TASK("assemble"),
  CHECK_DAIKON_TASK("daikonCheck"),
  CHECK_CHICORY_TASK("chicoryCheck"),
  CHECK_DYN_COMP_TASK("dyncompCheck"),
  CHECK_TASK("check");

  String value;
  Options(String value){
    this.value = value;
  }

  public String value() {
    return value;
  }

  @Override public String toString() {
    return name() + "->" + value();
  }
}
