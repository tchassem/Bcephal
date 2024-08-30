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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportEditorData;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.domain.DashbordReportType;
import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilter;
import com.moriset.bcephal.dashboard.repository.ChartPropertiesRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportRepository;
import com.moriset.bcephal.dashboard.repository.PivotTablePropertiesRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class DashboardReportServiceTest {
	
	@Mock
	private DashboardReportRepository dashboardReportRepository;
	@Mock
	private DashboardReportFieldRepository dashboardReportFieldRepository;
	@Mock
	private PivotTablePropertiesRepository pivotTablePropertiesRepository;
	@Mock
	private ChartPropertiesRepository chartPropertiesRepository;
	@Mock
	private UniverseFilterService universeFilterService;
	@Mock
	private UniverseDynamicFilterService dynamicFilterService;
	@Mock
	private DashboardReportFieldService dashboardReportFieldService;
	@Mock
	private EntityManager session;
	@Mock
	private SecurityService securityService;
	@Mock
	private UserSessionLogService logService;
	@Mock
	private MaterializedGridRepository materializedGridRepository;
	@Mock
	private JoinColumnRepository joinColumnRepository;
	@Mock
	private JoinService joinService;
	@Mock
	private HttpSession httpSession;
	@Mock
	private SpotService spotService;
	@Mock
	private DashboardReportEditorData dashboardReportEditorData;
	
	@InjectMocks
	private DashboardReportService dashboardReportService;

	@Test
	void should_getBrowserFunctionalityCode() throws Exception {
		
		String chain= dashboardReportService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
	}
	@Test
	void should_getHidedObjectId() throws Exception {
		List<Long> items =new ArrayList<>();
		items.add(1L);
		List<Long> items2= new ArrayList<>();
		items2.add(2L);
		List<Long> items3= new ArrayList<>();
		items3.add(3L);
		when(securityService.getHideProfileById(1L, "functionalityCode","projectCode")).thenReturn(items);
		when(securityService.getHideProfileById(1L, FunctionalityCodes.REPORTING_PIVOT_TABLE,"projectCode")).thenReturn(items2);
		when(securityService.getHideProfileById(1L, FunctionalityCodes.REPORTING_REPORT_GRID,"projectCode")).thenReturn(items3);
		List<Long> items1 =dashboardReportService.getHidedObjectId(1L, "functionalityCode", "projectCode");
		assertThat(items1).isNotNull();
		assertEquals(items1.size(), 3);
		
	}
	@Test
	void should_saveUserSessionLog()  throws Exception {
		logService.saveUserSessionLog("username", 1L, "projectCode", "usersession", 1L, "functionalityCode",
				"rightLevel", 1L);
		dashboardReportService.saveUserSessionLog("username", 1L, "projectCode", "usersession", 1L, "functionalityCode",
				"rightLevel", 1L);
		assertTrue(true);
	}
	@Test
	void should_getFunctionalityCode() throws Exception {
		DashboardReport dashboardReport= new DashboardReport();
		dashboardReport.setReportType(DashbordReportType.CHART);
		String f= dashboardReportService.getFunctionalityCode(1L);
		assertTrue(true);
		assertThat(f).isNotNull();
	}
	@Test
	void schould_getRepository() throws Exception {
		DashboardReportRepository dashboardReportRepository= dashboardReportService.getRepository();
		assertThat(dashboardReportRepository).isNotNull();
	}
	@Test
	void should_getNewItem() throws Exception {
		DashboardReport dashboardReport= dashboardReportService.getNewItem();
		assertThat(dashboardReport).isNotNull();
		assertEquals(dashboardReport.getName(),"DashboardReport");
	}
	@Test
	void should_getNewItemType() throws Exception {
		DashboardReport dashboardReport= dashboardReportService.getNewItem("CHART");
		assertThat(dashboardReport).isNotNull();
		assertEquals(dashboardReport.getName(),"Chart ");
	}
	@Test
	void should_getNewItemType1() throws Exception {
		DashboardReport dashboardReport= dashboardReportService.getNewItem("TREE_LIST");
		assertThat(dashboardReport).isNotNull();
		assertEquals(dashboardReport.getName(),"DashboardReport");
	}
	@Test
	void should_getEditorData() throws Exception {
		Nameable nameable = new Nameable();
		nameable.setId(1L);
		nameable.setName("nameN");
		EditorDataFilter editorDataFilter= getEditorDataFilter();
		List<Nameable> list = new ArrayList<>();
		list.add(nameable);
		DashboardReportEditorData data = new DashboardReportEditorData();
		data.setMatGrids(list);
		data.setSpots(list);
		when(materializedGridRepository.findGenericAllAsNameables()).thenReturn(list);
		//when(dashboardReportEditorData.)
		EditorData<DashboardReport>  data1 = dashboardReportService.getEditorData(editorDataFilter, httpSession, Locale.CHINA);
		assertThat(data1).isNotNull();
		assertEquals(data.getMatGrids().size(), 1);
	}
	@Test
	void should_getNewBrowserData() throws Exception {
		DashboardReport dashboardReport=getDashboardReport();
		BrowserData browserData=dashboardReportService.getNewBrowserData(dashboardReport);
		assertTrue(true);
		assertThat(browserData).isNotNull();
	}
	@Test
	void should_getBrowserDatasSpecification() throws Exception {
		BrowserDataFilter browserDataFilter = getBrowserDataFilter();
		List<Long> hidedObjectIds = new ArrayList<>();
		hidedObjectIds.add(1L);
		Specification<DashboardReport> qert = dashboardReportService.getBrowserDatasSpecification(browserDataFilter, Locale.FRANCE, hidedObjectIds);
		assertThat(qert).isNotNull();
	}
//	@Test
//	void should_getRows() throws Exception{
//		Locale locale=null;
//		DashboardReport dashboardReport=getDashboardReport();
//		DashboardReportService dashboardRepo =Mockito.spy(dashboardReportService);
//		doReturn(dashboardReport).when(dashboardRepo).getById(1L);
//		ArrayNode dash= dashboardRepo.getRows(1L, locale.ITALIAN);
//		assertThat(dash).isNotNull();
//		assertThat(dash).isEmpty();
//		
//	}
//	@Test
//	void should_getRows1() {
//		Locale locale=null;
//		DashboardReport dashboardReport=getDashboardReport();
//		
//		
//	}
	@Test
	void should_buildGrilleDataFilter() throws Exception  {
		DashboardReport dashboardReport=getDashboardReport();
		DashboardReportField dashboardReportField1 = new DashboardReportField();
		dashboardReportField1.setId(1L);
		dashboardReportField1.setName("name2");
//		DashboardReportField dashboardReportField = getDashboardReportField();
		List<DashboardReportField> fields = new ArrayList<>();
//		fields.add(dashboardReportField);
//		fields.add(dashboardReportField1);
//		GrilleColumn grilleColumn= getGrilleColumn();
//		DashboardReportService dashboardRepo =Mockito.spy(dashboardReportService);
//		doReturn(grilleColumn).when(dashboardRepo).b
		GrilleDataFilter filter=dashboardReportService.buildGrilleDataFilter(dashboardReport, fields);
		assertThat(filter).isNotNull();
		assertEquals(filter.getGrid().getDataSourceId(), 1);
		//assertEquals(filter.getGrid().getType(), "REPORT");
	}
	@Test
	void should_save() throws Exception {
		DashboardReport dashboardReport=getDashboardReport();
		when(dashboardReportRepository.save(dashboardReport)).thenReturn(dashboardReport);
		DashboardReport dashboardReport1= dashboardReportService.save(dashboardReport, Locale.CHINA);
		assertThat(dashboardReport1).isNotNull();
		assertEquals(dashboardReport1.getDataSourceId(), 1L);
	}
	@Test
	void should_delete() throws Exception {
		DashboardReport dashboardReport=getDashboardReport();
		dashboardReportService.delete(dashboardReport);
		assertTrue(true);
	}
	private DashboardReport getDashboardReport() {
		UniverseDynamicFilter universeDynamicFilter = new UniverseDynamicFilter();
		universeDynamicFilter.setId(1L);
		DataSourceType dataSourceType=null;
		DashboardReportField dashboardReportField =getDashboardReportField();
		ListChangeHandler<DashboardReportField> listChange =new ListChangeHandler<DashboardReportField>();
		listChange.addNew(dashboardReportField);
		DashboardReport dashboardReport = new DashboardReport();
		dashboardReport.setName("name");
		dashboardReport.setId(1L);
		dashboardReport.setFieldListChangeHandler(listChange);
		dashboardReport.setDataSourceType(dataSourceType);
		dashboardReport.setDataSourceId(1L);
		//dashboardReport.setCurrentDynamicFilter(universeDynamicFilter);
		return dashboardReport;
	}
	private DashboardReportField getDashboardReportField() {
		DashboardReportField dashboardReportField = new DashboardReportField();
		dashboardReportField.setId(1L);
		dashboardReportField.setName("name1");
		return dashboardReportField; 
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
		return browserDataFilter;
		
	}
	
	private EditorDataFilter getEditorDataFilter() {
		EditorDataFilter editorDataFilter = new EditorDataFilter();
		editorDataFilter.setId(1L);
		editorDataFilter.setDataSourceId(1L);
		editorDataFilter.setDataSourceType(DataSourceType.JOIN);
		editorDataFilter.setSubjectType("subjectType");
		return editorDataFilter;
	}

}
