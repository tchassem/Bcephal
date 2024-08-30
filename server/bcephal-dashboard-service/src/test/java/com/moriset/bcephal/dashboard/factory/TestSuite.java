package com.moriset.bcephal.dashboard.factory;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Dashboard Tests")
@SelectPackages({ "com.moriset.bcephal.dashboard.controller"})
public class TestSuite {

}
