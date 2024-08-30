package com.moriset.bcephal.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskEditorData;
import com.moriset.bcephal.task.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = TaskTemplateService.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})
public class TaskTemplateServiceTest {
	
	@Mock
	private HttpSession httpSession;
	@Mock
	private TaskService taskService;
	@Mock
	private TaskRepository taskRepository;
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private UserDataRepository userRepository;
	@Mock
	private ProfileDataRepository profileRepository;
	@Mock
	private MainObjectRepository<Task> getRepository;
	
	@Mock
	private IncrementalNumberRepository incrementalNumberRepository;
	
	@InjectMocks
	private TaskTemplateService taskTemplateService;
	

	@Test
	void should_getBrowserFunctionalityCode() throws Exception {
		String chain = taskTemplateService.getBrowserFunctionalityCode();
		assertThat(chain).isNotNull();
		assertEquals(chain, "task.template");
	}
	@Test
	void should_getEditorData() throws Exception {
		EditorDataFilter filter =getEditorDataFilter();
		Nameable nameable= new Nameable();
		nameable.setId(4L);
		nameable.setName("nameable4");
		List<Nameable> sequences= new ArrayList<>();
		sequences.add(nameable);
	    when(userRepository.findAllAsNameablesByClient(null)).thenReturn(sequences);
	    when(profileRepository.findAllAsNameablesByClient(null)).thenReturn(sequences);
		when(incrementalNumberRepository.getAllIncrementalNumbers()).thenReturn(sequences);
		TaskEditorData data = taskTemplateService.getEditorData(filter, httpSession, Locale.CANADA);
		assertThat(data).isNotNull();
		assertEquals(data.getSequences().size(), 1);
		
	}
	  
    @Test
    void should_getNewItem() throws Exception {
    	
    	Task task=taskTemplateService.getNewItem();
    	assertEquals(task.getName(), "Task template 1");
    }
  
	
	private EditorDataFilter getEditorDataFilter() {
		EditorDataFilter filter = new EditorDataFilter();
		filter.setId(1L);
		filter.setNewData(false );
		filter.setDataSourceId(1L);
		filter.setDataSourceType(DataSourceType.JOIN);
		return filter;
	}
	protected Task getTask() {
		Task task = new Task();
		task.setId(1L);
		task.setName("task");
		return task;
	}
	protected TaskEditorData getTaskEditorData() {
		TaskEditorData taskEditorData = new TaskEditorData();
		Nameable nameable= new Nameable();
		nameable.setId(1L);
		nameable.setName("nameable");
		List<Nameable> sequences= new ArrayList<>();
		sequences.add(nameable);
		taskEditorData.setSequences(sequences);
		return taskEditorData;
	}

}
