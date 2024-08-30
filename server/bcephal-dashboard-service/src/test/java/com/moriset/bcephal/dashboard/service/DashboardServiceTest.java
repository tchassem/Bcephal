package com.moriset.bcephal.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.dashboard.domain.DashboardBrowserData;
import com.moriset.bcephal.dashboard.domain.DashboardItem;
import com.moriset.bcephal.dashboard.domain.ProfileDashboard;
import com.moriset.bcephal.dashboard.domain.ProfileDashboardEditorData;
import com.moriset.bcephal.dashboard.repository.DashboardItemFilterRepository;
import com.moriset.bcephal.dashboard.repository.DashboardItemRepository;
import com.moriset.bcephal.dashboard.repository.DashboardRepository;
import com.moriset.bcephal.dashboard.repository.ProfileDashboardRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.utils.FunctionalityCodes;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class DashboardServiceTest {
	
	@Mock
	private DashboardRepository dashboardRepository;
	@Mock
	private DashboardItemRepository dashboardItemRepository;
	@Mock
	private DashboardItemFilterRepository dashboardItemFilterRepository;
	@Mock
	private ProfileDashboardRepository profileDashboardRepository;
	@Mock
	private SecurityService securityService;
	@Mock
	private UserSessionLogService logService;
	@Mock
	private FunctionalityCodes functionalityCodes;
	@Mock
	private MainObjectService<Dashboard, BrowserData> mainObjectService;
	
	@InjectMocks
	private DashboardService dashboardService;

	@Test
	void should_getBrowserFunctionalityCode() throws Exception {
		String func =dashboardService.getBrowserFunctionalityCode();
		assertThat(func).isNotNull();
		assertEquals(func,"dashboarding.dashboard");
	}
	@Test
	void should_getHidedObjectId() throws Exception {
		List<Long> lists = new ArrayList<>();
		lists.add(1L);
		lists.add(2L);
		when(securityService.getHideProfileById(1L, "functionalityCode", "projectCode")).thenReturn(lists);
		List<Long> lists1 = dashboardService.getHidedObjectId(1L,"functionalityCode", "projectCode");
		assertThat(lists1).isNotNull();
		assertEquals(lists1.size(), 2);
	}
	@Test
	void should_saveUserSessionLog() throws Exception {
		dashboardService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		assertTrue(true);
	}
	@Test
	void shoud_getRepository() throws Exception {
		DashboardRepository dashboardRepository = dashboardService.getRepository();
		assertTrue(true);
		assertThat(dashboardRepository).isNotNull();
	}
	@Test
	void should_getNewItem() throws Exception {
		Dashboard dashboard=dashboardService.getNewItem();
		assertTrue(true);
		assertEquals(dashboard.getName(), "Dashboard");
	}
	@Test
	void should_search() throws Exception {
		BrowserDataFilter browserDataFilter =getBrowserDataFilter();
		Dashboard dashboard=getDashboard();
		List<Dashboard> dashboards= new ArrayList<>();
		dashboards.add(dashboard);
		//when(profileDashboardRepository.getDashboardByProfileId(browserDataFilter.getProfileId())).thenReturn(dashboards);		
		BrowserDataPage<DashboardBrowserData> page1= dashboardService.search(browserDataFilter, Locale.ENGLISH, 1L, "projectCode");
		assertThat(page1).isNotNull();
		assertEquals(page1.getPageSize(), 5);
		
		
	}
	@Test
	void should_getNewBrowserData() throws Exception {
		BrowserData browserData = dashboardService.getNewBrowserData(getDashboard());
		assertThat(browserData).isNotNull();
	}
	@Test
	void should_save() throws Exception {
		Dashboard dashboard = getDashboard();
		when(dashboardRepository.save(dashboard)).thenReturn(dashboard);
		//when(dashboardItemRepository.save(dashboard.getDefaultItem())).thenReturn(dashboardItem);
		Dashboard dashboard1= dashboardService.save(dashboard, Locale.GERMAN);
		assertThat(dashboard1).isNotNull();
		assertEquals(dashboard1.getName(), "nameD");
		
	}
	@Test
	void shoud_delete()  throws Exception {
		Dashboard dashboard=getDashboard();
		dashboardService.delete(dashboard);
		assertTrue(true);
	}
	@Test
	void should_getProfileDashboardEditorData() throws Exception {
		ProfileDashboard profileDashboard = new ProfileDashboard();
		profileDashboard.setId(1L);
		profileDashboard.setName("profileDash");
		Dashboard dashboard=getDashboard();
		List<Dashboard> dashboards= new ArrayList<>();
		dashboards.add(dashboard);
		List<ProfileDashboard> listPrileDash = new ArrayList<>();
		listPrileDash.add(profileDashboard);
		when(profileDashboardRepository.findByProfileId(1L)).thenReturn(listPrileDash);
		when(dashboardRepository.findAll()).thenReturn(dashboards);
		ProfileDashboardEditorData data= dashboardService.getProfileDashboardEditorData(1L, Locale.CANADA);
		assertThat(data).isNotNull();
		assertEquals(data.getItemListChangeHandler().getItems().size(), 1);
	}
	@Test
	void should_save1() throws Exception {
		ProfileDashboard profileDashboard = new ProfileDashboard();
		profileDashboard.setId(1L);
		profileDashboard.setName("profileDash");
		ListChangeHandler<ProfileDashboard> dashboards = new ListChangeHandler<ProfileDashboard>();
		dashboards.addNew(profileDashboard);
		dashboardService.save(dashboards, 1L, Locale.KOREA);
		assertTrue(true);
	}
	
	
	private BrowserDataFilter getBrowserDataFilter() {
		DataSourceType dataSourceType=null;
		ColumnFilter columnFilter = new ColumnFilter();
		columnFilter.setDimensionId(1L);
		columnFilter.setName("nameColumn");
		BrowserDataFilter browserDataFilter = new BrowserDataFilter();
		browserDataFilter.setClientId(1L);
		browserDataFilter.setReportType("reportType");
		browserDataFilter.setPublished(true);
		browserDataFilter.setDataSourceType(dataSourceType);
		browserDataFilter.setCriteria("criteria");
		browserDataFilter.setColumnFilters(columnFilter);
		browserDataFilter.setPageSize(5);
		browserDataFilter.setProfileId(1L);
		return browserDataFilter;
		
	}
	private Dashboard getDashboard() {
		DashboardItem dashboardItem= new DashboardItem();
		dashboardItem.setId(1L);
		dashboardItem.setName("nameItem");
//		ListChangeHandler<DashboardItem> list = new ListChangeHandler<DashboardItem>();
//		list.addNew(dashboardItem);
		Dashboard dashboard = new Dashboard();
		dashboard.setId(1L);
		dashboard.setName("nameD");
		//dashboard.setItemsListChangeHandler(list);
		return dashboard;
	}
}
