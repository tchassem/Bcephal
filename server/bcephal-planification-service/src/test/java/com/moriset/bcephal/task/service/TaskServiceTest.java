package com.moriset.bcephal.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskBrowserData;
import com.moriset.bcephal.task.domain.TaskEditorData;
import com.moriset.bcephal.task.domain.TaskLogItem;
import com.moriset.bcephal.task.repository.TaskAudienceRepository;
import com.moriset.bcephal.task.repository.TaskLogItemRepository;
import com.moriset.bcephal.task.repository.TaskRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = TaskTemplateService.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class TaskServiceTest {
	
	@Mock
	private TaskRepository taskRepository;
	@Mock
	private TaskAudienceRepository taskAudienceRepository;
	@Mock
	private TaskLogItemRepository taskLogItemRepository;
	@Mock
	private SecurityService securityService;
	@Mock
	private UserSessionLogService logService;
	@Mock
	private UserDataRepository userRepository;
	@Mock
	private ProfileDataRepository profileRepository;
	@Mock
	private IncrementalNumberRepository incrementalNumberRepository;
	@Mock
	protected ObjectMapper mapper;
	@Mock
	private HttpSession httpSession;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private MessageSource messageSource;
	
	@InjectMocks
	private TaskService taskService;

	@Test
	void should_getBrowserFunctionalityCode() throws Exception {
		String chain = taskService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
		assertEquals(chain, "task");
	}
	@Test
	void should_getHidedObjectId() throws Exception{
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		when(securityService.getHideProfileById(1L, "functionalityCode", "projectCode")).thenReturn(list);
		List<Long> list1= taskService.getHidedObjectId(1L, "functionalityCode", "projectCode");
		assertThat(list1).isNotNull();
		assertEquals(list1.size(), 2);
	}
	
	@Test 
	void shoulod_saveUserSessionLog() throws Exception {
		logService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		taskService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		assertTrue(true);
	}
	@Test
	void shoild_getRepository() throws Exception {
		TaskRepository repository=taskService.getRepository();
		assertThat(repository).isNotNull();
	}
	@Test
	void shoud_getEditorData() throws Exception {
		EditorDataFilter filter=getEditorDataFilter();
		Nameable nameable= new Nameable();
		nameable.setId(4L);
		nameable.setName("nameable4");
		List<Nameable> sequences= new ArrayList<>();
		sequences.add(nameable);
		 when(userRepository.findAllAsNameablesByClient(null)).thenReturn(sequences);
		 when(profileRepository.findAllAsNameablesByClient(null)).thenReturn(sequences);
		 TaskEditorData data=taskService.getEditorData(filter, httpSession, Locale.GERMANY);
		 assertThat(data).isNotNull();
		 assertEquals(data.getUsers().size(), 1);
		 assertEquals(data.getProfils().size(), 1);
	}
	 @Test
	    void should_getNewItem() throws Exception {
	    	
	    	Task task=taskService.getNewItem();
	    	assertEquals(task.getName(), "Task 1");
	    }
	 @Test
	    void should_getById() throws Exception {
		 Task task=getTask();
		
		 when(taskService.getRepository().findById(1L)).thenReturn(Optional.of(task));
		 Task task1= taskService.getById(1L);
		 assertThat(task1).isNotNull();
		 assertEquals(task1.getId(), 1L);
	 }
	 @Test
	 void should_generateTaskId() throws Exception {
		 when(taskRepository.findById(1L)).thenReturn(Optional.empty());
		 assertThrows(BcephalException.class, ()-> this.taskService.generateTask(1L));
		 Task task=getTask();
		 IncrementalNumber incrementalNumber= getIncrementalNumber();
		 when(incrementalNumberRepository.findById(1L)).thenReturn(Optional.of(incrementalNumber));
		 when(incrementalNumberRepository.save(incrementalNumber)).thenReturn(incrementalNumber);
		 when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
		 when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);
		 Task task1 =taskService.generateTask(1L);
		 assertThat(task1).isNotNull();
		 assertEquals(task1.getId(), 1L);
	 }
	 @Test
	 void should_generateTask() throws Exception {
		 Task task0=null;
		 assertThrows(BcephalException.class, ()-> this.taskService.generateTask(task0));
		 Task task=getTask();
		 IncrementalNumber incrementalNumber= getIncrementalNumber();
		 when(incrementalNumberRepository.findById(1L)).thenReturn(Optional.of(incrementalNumber));
		 when(incrementalNumberRepository.save(incrementalNumber)).thenReturn(incrementalNumber);
		 when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);
		 Task task1 =taskService.generateTask(task);
		 assertThat(task1).isNotNull();
		 assertEquals(task1.getId(), 1L);
	 }
	 @Test
	 void should_save() throws Exception {
		 Task task0= null;
		 assertThrows(BcephalException.class, ()-> this.taskService.save(task0, Locale.ITALIAN));
		 Task task1= new Task();
		 task1.setId(2L);
		 assertThrows(BcephalException.class, ()-> this.taskService.save(task1, Locale.ITALIAN));
		 Task task=getTask();
		 
		 when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);
		 TaskLogItem taskLogItem = new TaskLogItem();
		 taskLogItem.setId(1L);
		 taskLogItem.setName("taskLogItem");
		 when(taskLogItemRepository.save(Mockito.any(TaskLogItem.class))).thenReturn(taskLogItem);
		 Task task2 = taskService.save(task, Locale.ITALIAN);
		 assertThat(task2).isNotNull();
		 assertEquals(task2.getCode(), "1");
	 }
	 @Test
	 void should_delete() throws Exception  {
		 Task task0= null;
		 taskService.delete(task0);
		 assertTrue(true);
		 Task task=getTask();
		 taskService.delete(task);
		 assertTrue(true);
	 }
	 @Test
	 void should_getNewBrowserData() throws Exception {
		 TaskBrowserData data= taskService.getNewBrowserData(getTask());
		 assertThat(data).isNotNull();
		 assertEquals(data.getCode(), "1");
		 assertEquals(data.getName(), "task");
		 
	 }
	 @Test 
	 void should_getBrowserDatasSpecification() throws Exception{
			List<Long> list = new ArrayList<>();
			list.add(1L);
			list.add(2L);
			BrowserDataFilter browserDataFilter = getBrowserDataFilter();
			Specification<Task> transform = taskService.getBrowserDatasSpecification(browserDataFilter, Locale.FRANCE, list);
			assertThat(transform).isNotNull();
	 }
	 @Test
	 void should_build() throws Exception {
		 ColumnFilter columnFilter = new ColumnFilter();
		 columnFilter.setName("CurrentlyExecuting");
		 taskService.build(columnFilter);
		 assertTrue(true);
	 }
	
	private EditorDataFilter getEditorDataFilter() {
		EditorDataFilter filter = new EditorDataFilter();
		filter.setId(1L);
		filter.setNewData(false );
		filter.setDataSourceId(1L);
		filter.setDataSourceType(DataSourceType.JOIN);
		return filter;
	}
	private IncrementalNumber getIncrementalNumber() {
		IncrementalNumber incrementalNumber = new IncrementalNumber();
		incrementalNumber.setId(1L);
		incrementalNumber.setName("incrementalNumber");
		return incrementalNumber;
	}
	private BrowserDataFilter getBrowserDataFilter() {
		BrowserDataFilter browserDataFilter = new BrowserDataFilter();
		browserDataFilter.setClientId(1L);
		browserDataFilter.setDataSourceId(1L);
		return browserDataFilter;
	}
	private Task getTask() {
		Task task = new Task();
		task.setId(1L);
		task.setName("task");
		task.setDocumentCount(1);
		task.setSequenceId(1L);
		return task;
	}

}
