/**
 * 6 févr. 2024 - UserLoaderServiceTests.java
 *
 */
package com.moriset.bcephal.scheduler.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.dashboard.service.PresentationChartService;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

/**
 * @author Emmanuel Emmeni
 *
 */
@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PresentationChartServiceTests {
	
	@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.loader.service", "com.moriset.bcephal.service", "com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config", 
				"com.moriset.bcephal.task.service", "com.moriset.scheduler", "com.moriset.bcephal.planification","com.moriset.bcephal.dashboard.service"
		},
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }
	)
	@EntityScan(
		basePackages = { 
			"com.moriset.bcephal.domain", "com.moriset.bcephal.security.domain","com.moriset.bcephal.dashboard"
		}
	)
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}
	

	@Autowired
	PresentationChartService presentationChartService;
	
	@Autowired
	MultiTenantInterceptor Interceptor;
	
	
    
    public void createPresentationFromLayout () throws Exception {
		FileInputStream in = new FileInputStream("D:\\layout2.pptx");
		XMLSlideShow layoutPpt = new XMLSlideShow(in);
		XSLFSlide layoutSlide = layoutPpt.getSlides().getFirst();
		
		
		//XMLSlideShow ppt = new XMLSlideShow();
		XMLSlideShow ppt = new XMLSlideShow(new FileInputStream("D:\\powerpoint.pptx"));
		XSLFSlide slide = ppt.createSlide(layoutSlide.getSlideLayout());
		slide.importContent(layoutSlide);
		
		//presentationChartService.buildChart(ppt, 59L);
		
		layoutPpt.close();
		FileOutputStream out = new FileOutputStream("D:\\powerpoint.pptx");
		ppt.write(out);
		out.close();
		ppt.close();
	}
    
    @Test
    @DisplayName("Test chart")
    @Order(1)
	public void ChartTest() throws Exception {
    	Interceptor.setTenantForServiceTest("P-2-BinSponsor24052023-s1R");
    	createPresentationFromLayout ();
	}

}
