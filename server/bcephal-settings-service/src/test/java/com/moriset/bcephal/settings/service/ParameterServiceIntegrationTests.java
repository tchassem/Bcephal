package com.moriset.bcephal.settings.service;

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

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.repository.ParameterRepository;

import jakarta.servlet.http.HttpSession;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParameterServiceIntegrationTests {
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.settings", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.settings.domain" })
@ActiveProfiles(profiles = { "int", "serv" })
static class ApplicationTests {
}

@Autowired
MultiTenantInterceptor Interceptor;
private String EntityTenantId = "bcephal_test";
private static Parameter parameter;


@Autowired ParameterService parameterService;
@Autowired ParameterRepository parameterRepo;
@Autowired HttpSession session;

@BeforeAll
static void init() {
	ListChangeHandler<Parameter> parameters = new ListChangeHandler<>();
	parameters.addNew(Parameter.builder().code("prametre").parameters(null).id(5L).build());
	parameter =Parameter.builder().code("billing.role").parameters(parameters).id(2L).build();
	
}

@Test
@DisplayName("Save a new Parameter")
@Order(1)
@Commit
void saveNewParameter() throws Exception {
	Interceptor.setTenantForServiceTest(EntityTenantId);
	assertThat(parameter).isNotNull();
	
	//parameterService.saveRoot(parameter, Locale.getDefault());
	parameterRepo.save(parameter);
	Parameter found = parameterService.getById(parameter.getId());
	assertThat(found).isNotNull();
	//assertThat(found.getCode()).isEqualTo(parameter.getCode());
}

@Test
@DisplayName("build automaticaly Parameter")
@Order(2)
@Commit
void buildAutoParameter() throws Exception {
	Interceptor.setTenantForServiceTest(EntityTenantId);
	assertThat(parameter).isNotNull();
	
	parameterService.buildAutomatically(parameter.getCode(), session , Locale.getDefault());
	List<Parameter> foundList = parameterRepo.findAll();
	assertThat(foundList).isNotEmpty();
}

@Test
@DisplayName("delete Parameter")
@Order(3)
@Commit
void deleteParameter() throws Exception {
	Interceptor.setTenantForServiceTest(EntityTenantId);
	assertThat(parameter).isNotNull();
	
	parameterService.delete(parameter);
	Parameter found = parameterService.getById(parameter.getId());
	assertThat(found).isNull();
}
}
