package com.moriset.bcephal.reporting;


import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;



@Suite
@SuiteDisplayName("Reporting test")
@SelectPackages({"com.moriset.bcephal.reporting.controller"})
@ExcludePackages({"com.moriset.bcephal.utils"})
public class TestSuite {

}
