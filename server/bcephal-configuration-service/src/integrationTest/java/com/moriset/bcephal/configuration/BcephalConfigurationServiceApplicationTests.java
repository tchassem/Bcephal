package com.moriset.bcephal.configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class BcephalConfigurationServiceApplicationTests {

	@LocalServerPort
	private int port;
	
	@Test
	void contextLoads() {
		assertTrue(port > 0, String.format("Wrong port : {0}", port));
	}

}
