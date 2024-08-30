package com.moriset.planification;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Planification Tests")
@SelectPackages({"com.moriset.bcephal.planification.controller"})
public class TestSuite {

}
