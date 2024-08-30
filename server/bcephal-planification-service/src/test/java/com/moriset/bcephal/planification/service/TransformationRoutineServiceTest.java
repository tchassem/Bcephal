package com.moriset.bcephal.planification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.repository.SmartMaterializedGridRepository;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineItem;
import com.moriset.bcephal.planification.repository.TransformationRoutineCalculateItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineConcatenateItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineFieldRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineItemRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineMappingRepository;
import com.moriset.bcephal.planification.repository.TransformationRoutineRepository;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.filters.CalendarRepository;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;



@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class TransformationRoutineServiceTest {
	@Mock 
	private TransformationRoutineRepository routineRepository;	
	@Mock 
	private TransformationRoutineItemRepository routineItemRepository;
	@Mock 
	private TransformationRoutineFieldRepository routineFieldRepository;
	@Mock 
	private TransformationRoutineCalculateItemRepository routineCalculateItemRepository;
	@Mock 
	private TransformationRoutineConcatenateItemRepository routineConcatenateItemRepository;
	@Mock 
	private TransformationRoutineMappingRepository routineMappingRepository;
	@Mock 
	private UniverseFilterService universeFilterService;
	@Mock 
	private GrilleRepository grilleRepository;
	@Mock 
	private SecurityService securityService;
	@Mock 
	private UserSessionLogService logService;
	@Mock 
	private MaterializedGridRepository materializedGridRepository;
	@Mock
	private MessageSource messageSource;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private HttpSession httpSession;
	@Mock
	private SmartMaterializedGridRepository smartMaterializedGridRepository;
	@Mock
	private CalendarRepository calendarRepository;
	@Mock
	private SpotRepository spotRepository;
	@Mock
	private InitiationService initiationService;
	
	@InjectMocks
	private TransformationRoutineService transformationRoutineService;
	
	@Test
    void should_getRepositoryTest() throws Exception {
		MainObjectRepository<TransformationRoutine> transformationRoutineRepository=transformationRoutineService.getRepository();
    	assertTrue(true);
    	assertThat(transformationRoutineRepository).isNotNull();
    }
	

	@Test
	void should_save() throws Exception  {
		TransformationRoutine transformationRoutine0=null;
		TransformationRoutine transformationRoutine=getTransformationRoutine();
		UniverseFilter universeFilter = new UniverseFilter();
		universeFilter.setId(1L);
	
		assertThrows(BcephalException.class, ()-> this.transformationRoutineService.save(transformationRoutine0, Locale.CANADA));
		TransformationRoutineService mock= Mockito.mock(TransformationRoutineService.class);
		when(mock.save(transformationRoutine, Locale.CANADA)).thenReturn(transformationRoutine);
		TransformationRoutine transformationRoutine1= mock.save(transformationRoutine, Locale.CANADA);
		assertThat(transformationRoutine1).isNotNull();
		assertEquals(transformationRoutine1.getId(), 1L);
		assertEquals(transformationRoutine1.getItemListChangeHandler().getNewItems().size(), 1);
	}
	@Test
	void should_delete() throws Exception {
		TransformationRoutine transformationRoutine=getTransformationRoutine();
		transformationRoutineService.delete(transformationRoutine);
		assertTrue(true);
	}
	@Test
	void should_getEditorData() throws Exception{
		
		EditorDataFilter editorDataFilter=getEditorDataFilter();
		Nameable nameable = new Nameable();
		nameable.setId(1L);
		nameable.setName("nameable");
		List<Nameable> listNameable= new ArrayList<>();
		listNameable.add(nameable);
		SmartMaterializedGrid smartMaterializedGrid= new SmartMaterializedGrid();
		smartMaterializedGrid.setId(1L);
		smartMaterializedGrid.setName("smartMaterializedGrid");
		
	   when(grilleRepository.findByType(GrilleType.INPUT)).thenReturn(listNameable);
	    when(materializedGridRepository.findGenericAllAsNameables()).thenReturn(listNameable);
		EditorData<TransformationRoutine> data= transformationRoutineService.getEditorData(editorDataFilter, httpSession, Locale.FRANCE);
		assertThat(data).isNotNull();
		
	
	}
	
	@Test 
	void should_getNewItem() throws Exception {
		TransformationRoutine u=transformationRoutineService.getNewItem();
    	assertEquals(u.getName(), "Routine 1");
	}
	
	@Test
	void should_getNewBrowserData() throws Exception {
		BrowserData browserData= transformationRoutineService.getNewBrowserData(getTransformationRoutine());
		assertThat(browserData).isNotNull();
		assertEquals(browserData.getName(), "transformationRoutine");
		
	}
	@Test
	void should_getBrowserDatasSpecification() throws Exception {
		
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		BrowserDataFilter browserDataFilter = getBrowserDataFilter();
		Specification<TransformationRoutine> transform = transformationRoutineService.getBrowserDatasSpecification(browserDataFilter, Locale.FRANCE, list);
		assertThat(transform).isNotNull();
		
	}
	@Test
	void should_getBrowserFunctionalityCode() throws Exception {
		String chain= transformationRoutineService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
		assertEquals(chain, "transformation.routine");
	}
	@Test
	void should_getHidedObjectId() throws Exception {
		
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		when(securityService.getHideProfileById(1L, "functionalityCode", "projectCode")).thenReturn(list);
		List<Long> list1= transformationRoutineService.getHidedObjectId(1L, "functionalityCode", "projectCode");
		assertThat(list1).isNotNull();
		assertEquals(list1.size(), 2);
	}
	@Test
	void should_saveUserSessionLog() throws Exception {
		logService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		transformationRoutineService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		assertTrue(true);
	}
	private TransformationRoutine getTransformationRoutine() {
		TransformationRoutine transformationRoutine = new TransformationRoutine();
		UniverseFilter universeFilter = new UniverseFilter();
		universeFilter.setId(1L);
		TransformationRoutineItem transformationRoutineItem= new TransformationRoutineItem();
		transformationRoutineItem.setId(1L);
		transformationRoutineItem.setName("transformationRoutineItem");
		List<TransformationRoutineItem> list = new ArrayList<>();
		list.add(transformationRoutineItem);
		ListChangeHandler<TransformationRoutineItem> items= new ListChangeHandler<TransformationRoutineItem>();
		items.setNewItems(list);
		transformationRoutine.setId(1L);
		transformationRoutine.setFilter(universeFilter);
		transformationRoutine.setItemListChangeHandler(items);
		transformationRoutine.setName("transformationRoutine");
		return transformationRoutine;
	}
	
	private EditorDataFilter getEditorDataFilter() {
		EditorDataFilter editorDataFilter = new EditorDataFilter();
		editorDataFilter.setId(1L);
		editorDataFilter.setDataSourceId(1L);
		//editorDataFilter.setNewData(true);
		editorDataFilter.setDataSourceId(1L);
		editorDataFilter.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
		return editorDataFilter;
	}
	private BrowserDataFilter getBrowserDataFilter() {
		BrowserDataFilter browserDataFilter = new BrowserDataFilter();
		browserDataFilter.setClientId(1L);
		browserDataFilter.setDataSourceId(1L);
		return browserDataFilter;
	}
	

}
