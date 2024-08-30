package com.moriset.bcephal.sourcing.grid;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Sourcing Tests")
@SelectPackages({"com.moriset.bcephal.grid.service", "com.moriset.bcephal.sourcing.grid.controller"})
public class TestSuite {

}
