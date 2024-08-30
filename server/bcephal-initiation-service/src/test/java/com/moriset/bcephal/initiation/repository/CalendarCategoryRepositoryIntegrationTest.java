package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.initiation.domain.CalendarCategory;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarCategoryRepositoryIntegrationTest {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	
	@Autowired CalendarRepository calendarRepository;
	private static CalendarCategory calendarCategory;
	
	@BeforeAll
	static void init() {
		calendarCategory = CalendarCategory.builder().build();
		
				}
	
	@Test
	@DisplayName("save new calendar test")
	@Order(1)
	@Commit
	void saveNewCalendarTest() {
		assertThat(calendarCategory).isNotNull();
		calendarRepository.save(calendarCategory);
		Optional<CalendarCategory> found = calendarRepository.findById(calendarCategory.getId());
		assertThat(found).isNotEmpty();
		assertEquals(found.get().getId(), calendarCategory.getId());
	}
	
	@Test
	@DisplayName("update calendar test")
	@Order(2)
	@Commit
	void updateCalendarTest() {
		assertThat(calendarCategory).isNotNull();
		calendarCategory.setName("updatedInfo");
		calendarRepository.save(calendarCategory);
		Optional<CalendarCategory> found = calendarRepository.findById(calendarCategory.getId());
		assertThat(found).isNotEmpty();
		assertEquals(found.get().getName(), calendarCategory.name);
	}
	
	@Test
	@DisplayName("find calendar by id test")
	@Order(2)
	@Commit
	void findCalendarByTestTest() {
		assertThat(calendarCategory).isNotNull();
		Optional<CalendarCategory> found = calendarRepository.findById(calendarCategory.getId());
		assertThat(found).isNotEmpty();
		assertEquals(found.get().getName(), calendarCategory.name);
	}
	
	
	@Test
	@DisplayName("delete calendar test")
	@Order(10)
	@Commit
	void deleteCalendarTest() {
		assertThat(calendarCategory).isNotNull();
		calendarRepository.delete(calendarCategory);
		Optional<CalendarCategory> found = calendarRepository.findById(calendarCategory.getId());
		assertThat(found).isEmpty();
	}
	

}
