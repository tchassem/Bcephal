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
import com.moriset.bcephal.task.domain.TaskLogItem;
import com.moriset.bcephal.task.domain.TaskLogItemBrowserData;
import com.moriset.bcephal.task.repository.TaskLogItemRepository;
import com.moriset.bcephal.utils.BcephalException;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = TaskTemplateService.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class TaskLogItemServiceTest {
	
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
	private MessageSource messageSource;
	@InjectMocks
	private TaskLogItemService taskLogItemService;

	@Test
	void should_getBrowserFunctionalityCode()  throws Exception {
		String chain = taskLogItemService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
		assertEquals(chain, "task.log.item");
	}
	@Test
	void should_getHidedObjectId() throws Exception{
		logService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		taskLogItemService.saveUserSessionLog("username",1L,"projectCode", "usersession", 1L, "functionalityCode","rightLevel", 1L);
		assertTrue(true);
	}
	@Test
	void should_getRepository() throws Exception {
		TaskLogItemRepository repository=taskLogItemService.getRepository();
		assertThat(repository).isNotNull();
	}
	 @Test
	    void should_getNewItem() throws Exception {
	    	
		 TaskLogItem taskLogItem=taskLogItemService.getNewItem();
	    	assertEquals(taskLogItem.getName(), "Task log item 1");
	    }
	 @Test
	 void should_save() throws Exception{
		 TaskLogItem taskLogItem0 = null;
		 assertThrows(BcephalException.class, ()-> this.taskLogItemService.save(taskLogItem0, Locale.ITALIAN));
		 TaskLogItem taskLogItem=getTaskLogItem();
		 when(taskLogItemRepository.save(Mockito.any(TaskLogItem.class))).thenReturn(taskLogItem);
		 TaskLogItem taskLogItem1= taskLogItemService.save(taskLogItem, Locale.ITALIAN);
		 assertThat(taskLogItem1).isNotNull();
		 assertEquals(taskLogItem1.getName(), "taskLogItem");
	 }
	 @Test
	 void should_delete() throws Exception  {
		 TaskLogItem taskLogItem0= null;
		 taskLogItemService.delete(taskLogItem0);
		 assertTrue(true);
		 TaskLogItem taskLogItem=getTaskLogItem();
		 taskLogItemService.delete(taskLogItem);
		 assertTrue(true);
	 }
	 @Test
	 void should_getNewBrowserData() throws Exception {
		 TaskLogItemBrowserData data= taskLogItemService.getNewBrowserData(getTaskLogItem());
		 assertThat(data).isNotNull();
		 assertEquals(data.getUsername(), "usertaskLogItem");
		 assertEquals(data.getName(), "taskLogItem");
		 
	 }
	 @Test 
	 void should_getBrowserDatasSpecification() throws Exception{
			List<Long> list = new ArrayList<>();
			list.add(1L);
			list.add(2L);
			BrowserDataFilter browserDataFilter = getBrowserDataFilter();
			Specification<TaskLogItem> transform = taskLogItemService.getBrowserDatasSpecification(browserDataFilter, Locale.FRANCE, list);
			assertThat(transform).isNotNull();
	 }
	 @Test
	 void should_build() throws Exception {
		 ColumnFilter columnFilter = new ColumnFilter();
		 columnFilter.setName("CurrentlyExecuting");
		 taskLogItemService.build(columnFilter);
		 assertTrue(true);
	 }
	 private BrowserDataFilter getBrowserDataFilter() {
			BrowserDataFilter browserDataFilter = new BrowserDataFilter();
			browserDataFilter.setClientId(1L);
			browserDataFilter.setDataSourceId(1L);
			return browserDataFilter;
		}
	 
	 private TaskLogItem getTaskLogItem() {
		 TaskLogItem taskLogItem = new TaskLogItem();
		 taskLogItem.setId(1L);
		 taskLogItem.setName("taskLogItem");
		 taskLogItem.setTaskId(2L);
		 taskLogItem.setUsername("usertaskLogItem");
		 return taskLogItem;
	 }


}
