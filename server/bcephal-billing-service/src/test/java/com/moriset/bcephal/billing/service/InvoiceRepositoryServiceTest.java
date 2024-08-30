package com.moriset.bcephal.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.grid.domain.GrilleType;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class InvoiceRepositoryServiceTest {
	
	@InjectMocks
	private InvoiceRepositoryService invoiceRepositoryService;

	@Test
	void should_getGrilleType() throws Exception {
		GrilleType grilleType = invoiceRepositoryService.getGrilleType();
		assertThat(grilleType).isNotNull();
		assertEquals(grilleType.name(), "INVOICE_REPOSITORY");
	}
	
	@Test
	void should_getRepository() throws Exception{
		String  dto = invoiceRepositoryService.getRepositoryBaseName();
		assertThat(dto).isNotNull();
		assertEquals(dto, "Invoice repository ");
	}
	
	@Test
	void should_getRepositoryCode() throws Exception{
		String  dto = invoiceRepositoryService.getRepositoryCode();
		assertThat(dto).isNotNull();
		assertEquals(dto, "billing.invoice.repository.grid");
	}

}
