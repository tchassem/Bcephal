package com.moriset.bcephal.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilter;
import com.moriset.bcephal.dashboard.repository.UniverseDynamicFilterItemRepository;
import com.moriset.bcephal.dashboard.repository.UniverseDynamicFilterRepository;
import com.moriset.bcephal.service.filters.AttributeFilterService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class UniverseDynamicFilterServiceTest {
	
	@Mock
	private UniverseDynamicFilterRepository dynamicFilterRepository;
	@Mock
	private DynamicPeriodFilterService dynamicPeriodFilterService;
	@Mock
	private AttributeFilterService attributeFilterService;
	@Mock
	private UniverseDynamicFilterItemRepository itemRepository;
	@InjectMocks
	private UniverseDynamicFilterService universeDynamicFilterService;
	

	@Test
	void should_getRepository() throws Exception {
		UniverseDynamicFilterRepository universeDynamicFilterRepository= universeDynamicFilterService.getRepository();
		assertThat(universeDynamicFilterRepository).isNotNull();
	}
	@Test
	void should_save() throws Exception {
		UniverseDynamicFilter universeDynamicFilter= getUniverseDynamicFilter();
		when(dynamicFilterRepository.save(universeDynamicFilter)).thenReturn(universeDynamicFilter);
		UniverseDynamicFilter universeDynamicFilter1 = universeDynamicFilterService.save(universeDynamicFilter, Locale.ITALIAN);
		assertThat(universeDynamicFilter1).isNotNull();
		assertEquals(universeDynamicFilter1.getId(), 1L);
		
	}
	@Test
	void should_delete() throws Exception {
		UniverseDynamicFilter universeDynamicFilter= getUniverseDynamicFilter();
		universeDynamicFilterService.delete(universeDynamicFilter);
		assertTrue(true);
		
	}
	
	private UniverseDynamicFilter getUniverseDynamicFilter() {
		UniverseDynamicFilter universeDynamicFilter = new UniverseDynamicFilter();
		universeDynamicFilter.setId(1L);
		return universeDynamicFilter;
		
	}

}
