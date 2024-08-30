package com.moriset.bcephal.settings.config;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Settings tests")
@SelectPackages("com.moriset.bcephal.settings.controller")
public class TestSuite {

}
