package com.moriset.bcephal.initiation;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Initiation Tests")
@SelectPackages({"com.moriset.bcephal.initiation.controller"})
public class TestSuite {

}
