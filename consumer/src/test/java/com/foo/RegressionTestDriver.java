package com.foo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RegressionTestDriver {
  public static void main(String... args) {
    final FooStuffTest a = new FooStuffTest();
    a.testEquality();
  }
}