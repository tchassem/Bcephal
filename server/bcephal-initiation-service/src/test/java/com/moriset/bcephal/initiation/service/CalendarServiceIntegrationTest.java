package com.moriset.bcephal.initiation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.initiation.InitiationFactory;
import com.moriset.bcephal.initiation.domain.CalendarCategory;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarServiceIntegrationTest {

	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.initiation",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}, 
			exclude = {
					DataSourceAutoConfiguration.class, 
					HibernateJpaAutoConfiguration.class
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}

	
	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_initiation_test";
	
	@Autowired CalendarService calendarCategoryService;
	private static CalendarCategory calendarCategory;
	
	@BeforeAll
	static void init() {
		
		calendarCategory = new InitiationFactory().buildCalendarCategory();
	}
	
	
	@Test
	@DisplayName("save a new calendarCategory")
	@Order(1)
	@Commit
	void saveNewCalendar() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(calendarCategory).isNotNull();
		
		calendarCategory = calendarCategoryService.save(calendarCategory, Locale.getDefault());
		assertThat(calendarCategory.isPersistent()).isTrue();
		assertThat(calendarCategory.getId()).isNotNull();
	}
	
	
	@Test
	@DisplayName("get All calendarCategory")
	@Order(2)
	@Commit
	void getAllCalendar() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(calendarCategory).isNotNull();
		
		List<CalendarCategory> calendarCategories = calendarCategoryService.getCalendars(Locale.getDefault());
		assertThat(calendarCategories).isNotEmpty();
	}
	
	@Test
	@DisplayName("delete calendarCategory")
	@Order(3)
	@Commit
	void deleteCalendar() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(calendarCategory).isNotNull();
		
		calendarCategoryService.delete(calendarCategory, Locale.getDefault());
		CalendarCategory found = calendarCategoryService.getById(calendarCategory.getId());
		assertThat(found).isNull();
	}
}
