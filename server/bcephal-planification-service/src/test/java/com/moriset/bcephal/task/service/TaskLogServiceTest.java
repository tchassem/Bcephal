package com.moriset.bcephal.task.service;

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
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.task.domain.TaskLog;
import com.moriset.bcephal.task.domain.TaskLogBrowserData;
import com.moriset.bcephal.task.repository.TaskLogRepository;
import com.moriset.bcephal.utils.BcephalException;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = TaskTemplateService.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class TaskLogServiceTest {
	
	@Mock
	private TaskLogRepository taskLogRepository;
	@Mock
	private SecurityService securityService;
	@Mock
	private UserSessionLogService logService;
	@Mock
	private UserDataRepository userRepository;
	@Mock
	private ProfileDataRepository profileRepository;
	@Mock
	private MessageSource messageSource;
	@InjectMocks
	private TaskLogService taskLogService;

	@Test
	void should_getBrowserFunctionalityCode()  throws Exception {
		String chain = taskLogService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
		assertEquals(chain, "task.log");
	}
	@Test
	void should_getHidedObjectId() throws Exception{
		logService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		taskLogService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		assertTrue(true);
	}
	@Test
	void shoild_getRepository() throws Exception {
		TaskLogRepository repository=taskLogService.getRepository();
		assertThat(repository).isNotNull();
	}
	 @Test
	    void should_getNewItem() throws Exception {
	    	
		 TaskLog taskLog=taskLogService.getNewItem();
	    	assertEquals(taskLog.getName(), "Task log 1");
	    }
	 @Test
	 void should_save() throws Exception{
		 TaskLog taskLog0 = null;
		 assertThrows(BcephalException.class, ()-> this.taskLogService.save(taskLog0, Locale.ITALIAN));
		 TaskLog taskLog=getTaskLog();
		 when(taskLogRepository.save(Mockito.any(TaskLog.class))).thenReturn(taskLog);
		 TaskLog taskLog1= taskLogService.save(taskLog, Locale.ITALIAN);
		 assertThat(taskLog1).isNotNull();
		 assertEquals(taskLog1.getName(), "taskLog");
	 }
	 @Test
	 void should_delete() throws Exception  {
		 TaskLog taskLog0= null;
		 taskLogService.delete(taskLog0);
		 assertTrue(true);
		 TaskLog taskLog=getTaskLog();
		 taskLogService.delete(taskLog);
		 assertTrue(true);
	 }
	 @Test
	 void should_getNewBrowserData() throws Exception {
		 TaskLogBrowserData data= taskLogService.getNewBrowserData(getTaskLog());
		 assertThat(data).isNotNull();
		 assertEquals(data.getUsername(), "userTaskLog");
		 assertEquals(data.getName(), "taskLog");
		 
	 }
	 @Test 
	 void should_getBrowserDatasSpecification() throws Exception{
			List<Long> list = new ArrayList<>();
			list.add(1L);
			list.add(2L);
			BrowserDataFilter browserDataFilter = getBrowserDataFilter();
			Specification<TaskLog> transform = taskLogService.getBrowserDatasSpecification(browserDataFilter, Locale.FRANCE, list);
			assertThat(transform).isNotNull();
	 }
	 @Test
	 void should_build() throws Exception {
		 ColumnFilter columnFilter = new ColumnFilter();
		 columnFilter.setName("CurrentlyExecuting");
		 taskLogService.build(columnFilter);
		 assertTrue(true);
	 }
	 private BrowserDataFilter getBrowserDataFilter() {
			BrowserDataFilter browserDataFilter = new BrowserDataFilter();
			browserDataFilter.setClientId(1L);
			browserDataFilter.setDataSourceId(1L);
			return browserDataFilter;
		}
	 
	 private TaskLog getTaskLog() {
		 TaskLog taskLog = new TaskLog();
		 taskLog.setId(1L);
		 taskLog.setName("taskLog");
		 taskLog.setTaskId(2L);
		 taskLog.setUsername("userTaskLog");
		 return taskLog;
	 }

}
