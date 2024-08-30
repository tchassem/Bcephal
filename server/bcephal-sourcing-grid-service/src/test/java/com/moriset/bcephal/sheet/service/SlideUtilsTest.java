package com.moriset.bcephal.sheet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SlideUtilsTest {

	@Test
	@DisplayName("Create presentation")
	public void createPresentation() throws Exception {
		new SlideUtils().createPresentationFromLayout();
	}
	
}
