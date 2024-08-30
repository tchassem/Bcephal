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

import com.moriset.bcephal.initiation.domain.CalendarDay;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarDayRepositoryIntegrationTest {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	
	@Autowired CalendarDayRepository calendarDayRepository;
	private static CalendarDay calendarDay;
	
	@BeforeAll
	private static void init() {
		calendarDay = CalendarDay.builder().build();
		}
	

	@Test
	@DisplayName("save new Calendar")
	@Order(1)
	@Commit
	void saveNewCalendarTest() throws Exception{
		assertThat(calendarDay).isNotNull();
		
		CalendarDay cal= calendarDayRepository.save(calendarDay);
		Optional<CalendarDay> found = calendarDayRepository.findById(cal.getId());
		assertThat(found).isNotNull();
		assertEquals(found.get().getId(), calendarDay.getId());
	}
	@Test
	@DisplayName("update Calendar")
	@Order(2)
	@Commit
	void updateCalendarTest() throws Exception{
		assertThat(calendarDay).isNotNull();
		calendarDay.setDay(5);
		CalendarDay cal= calendarDayRepository.save(calendarDay);
		Optional<CalendarDay> found = calendarDayRepository.findById(cal.getId());
		assertThat(found).isNotNull();
		assertEquals(cal.getDay(), calendarDay.getDay());
	}
	@Test
	@DisplayName("find Calendar by id")
	@Order(3)
	@Commit
	void findCalendarByIdTest() throws Exception{
		assertThat(calendarDay).isNotNull();
		
		Optional<CalendarDay> calFound= calendarDayRepository.findById(calendarDay.getId());
		assertThat(calFound).isNotEmpty();
		assertEquals(calFound.get().getId(), calendarDay.getId());
	}
	
	@Test
	@DisplayName("delete Calendar")
	@Order(10)
	@Commit 
	void deleteCalendarTest() throws Exception{
		assertThat(calendarDay).isNotNull();
		calendarDayRepository.deleteById(calendarDay.getId());
		Optional<CalendarDay> found = calendarDayRepository.findById(calendarDay.getId());
		assertThat(found).isNotPresent();
		
		
	}
	
}
