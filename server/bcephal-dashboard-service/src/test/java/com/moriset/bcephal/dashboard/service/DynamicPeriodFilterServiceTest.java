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

import com.moriset.bcephal.dashboard.domain.DynamicPeriodFilter;
import com.moriset.bcephal.dashboard.domain.DynamicPeriodFilterItem;
import com.moriset.bcephal.dashboard.repository.DynamicPeriodFilterItemRepository;
import com.moriset.bcephal.dashboard.repository.DynamicPeriodFilterRepository;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.repository.filters.PeriodFilterItemRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class DynamicPeriodFilterServiceTest {
	
	@Mock
	private PeriodFilterItemRepository periodFilterItemRepository;;
	@Mock
	private DynamicPeriodFilterItemRepository dynamicPeriodFilterItemRepository;
	@Mock
	private DynamicPeriodFilterRepository dynamicPeriodFilterRepository;
	
	@InjectMocks
	private DynamicPeriodFilterService dynamicPeriodFilterService;
	
	@Test
	void should_getRepository() throws Exception{
		DynamicPeriodFilterRepository dynamicPeriodFilterRepository= dynamicPeriodFilterService.getRepository();
		assertThat(dynamicPeriodFilterRepository).isNotNull();
	}
	@Test
	void should_save() throws Exception {
		DynamicPeriodFilter dynamicPeriodFilter=getDynamicPeriodFilter();
		when(dynamicPeriodFilterRepository.save(dynamicPeriodFilter)).thenReturn(dynamicPeriodFilter);
		DynamicPeriodFilter dynamicPeriodFilter1= dynamicPeriodFilterService.save(dynamicPeriodFilter, Locale.ITALIAN);
		assertThat(dynamicPeriodFilter1).isNotNull();
		assertEquals(dynamicPeriodFilter1.getId(), 1L);
	}
	@Test
	void should_deleteDynamicPeriodFilterItem()  throws Exception {
		DynamicPeriodFilter dynamicPeriodFilter=getDynamicPeriodFilter();
		dynamicPeriodFilterService.delete(dynamicPeriodFilter);
		assertTrue(true);
	}
	
	private DynamicPeriodFilter getDynamicPeriodFilter() {
		ListChangeHandler<DynamicPeriodFilterItem> itemListChangeHandler= new ListChangeHandler<DynamicPeriodFilterItem>();
		itemListChangeHandler.addNew(getDynamicPeriodFilterItem());
		DynamicPeriodFilter dynamicPeriodFilter = new DynamicPeriodFilter();
		dynamicPeriodFilter.setId(1L);
		//dynamicPeriodFilter.setItemListChangeHandler(itemListChangeHandler);
		return dynamicPeriodFilter;
	}
	private DynamicPeriodFilterItem getDynamicPeriodFilterItem() {
		DynamicPeriodFilterItem dynamicPeriodFilterItem = new DynamicPeriodFilterItem();
		dynamicPeriodFilterItem.setId(1L);
		//dynamicPeriodFilterItem.setFilter(1L);
		return dynamicPeriodFilterItem;
		
	}
 
}
