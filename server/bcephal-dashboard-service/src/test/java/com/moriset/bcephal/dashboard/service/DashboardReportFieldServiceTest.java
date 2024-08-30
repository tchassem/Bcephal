package com.moriset.bcephal.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.domain.DashboardReportFieldProperties;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldPropertiesRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class DashboardReportFieldServiceTest {

	@Mock 
	private DashboardReportFieldPropertiesRepository dashboardReportFieldPropertiesRepository;
	
	@Mock 
	private DashboardReportFieldRepository dashboardReportFieldRepository;
	
	@InjectMocks
	private DashboardReportFieldService dashboardReportFieldService;
	
	@Test
	void should_getRepository() throws Exception {
		DashboardReportFieldRepository dashboardReportFieldRepository	= dashboardReportFieldService.getRepository();
		assertTrue(true);
		assertThat(dashboardReportFieldRepository).isNotNull();
	}
	
	@Test
	void should_save() throws Exception {
		Locale locale = Locale.ENGLISH;
		DashboardReportFieldProperties dashboardReportFieldProperties= getDashboardReportFieldProperties();
		DashboardReportField dashboardReportField= getDashboardReportField();
		when(dashboardReportFieldRepository.save(dashboardReportField)).thenReturn(dashboardReportField);
		when(dashboardReportFieldPropertiesRepository.save(dashboardReportField.getProperties())).thenReturn(dashboardReportFieldProperties);
		DashboardReportField dashboardReportField1 = dashboardReportFieldService.save(dashboardReportField, locale);
		
		assertThat(dashboardReportField1).isNotNull();
		
	}
	
	@Test
	void shoul_delete() throws Exception {
		DashboardReportField dashboardReportField= getDashboardReportField();
		doNothing().when(dashboardReportFieldRepository).deleteById(dashboardReportField.getId());
		dashboardReportFieldService.delete(dashboardReportField);
		assertTrue(true);
	}
	
	private DashboardReportField getDashboardReportField() {
		DashboardReportField dashboardReportField = new DashboardReportField();
		dashboardReportField.setId(1L);
		dashboardReportField.setName("name1");
		dashboardReportField.setProperties(getDashboardReportFieldProperties());
		return dashboardReportField;
	}
    private DashboardReportFieldProperties getDashboardReportFieldProperties() {
    	DashboardReportFieldProperties dashboardReportFieldProperties = new DashboardReportFieldProperties();
    	dashboardReportFieldProperties.setId(1L);
    	return dashboardReportFieldProperties;
    }
	

}
