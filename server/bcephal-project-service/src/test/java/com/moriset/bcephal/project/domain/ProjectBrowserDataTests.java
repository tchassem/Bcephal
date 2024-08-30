/**
 * 
 */
package com.moriset.bcephal.project.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.security.domain.ProjectBrowserData;

public class ProjectBrowserDataTests {

	@Test
	void validateProjectBrowserData() throws JsonProcessingException {
		ProjectBrowserData project = buildProjectBrowserData();
		Assertions.assertThat(project).isNotNull();
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(project);		
		Assertions.assertThat(json).isNotNull();
		Assertions.assertThat(json).isNotBlank();
		
		ProjectBrowserData data = mapper.readValue(json, ProjectBrowserData.class);
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.getCode()).isEqualTo(project.getCode());
		Assertions.assertThat(data.getName()).isEqualTo(project.getName());
		Assertions.assertThat(data.getSubscriptionId()).isEqualTo(project.getSubscriptionId());
		Assertions.assertThat(data.isDefaultProject()).isEqualTo(project.isDefaultProject());
	}
	
	ProjectBrowserData buildProjectBrowserData() {
		ProjectBrowserData project = ProjectBrowserData.builder()
				.code("" + System.currentTimeMillis())
				.name("My first project")
				.subscriptionId(1L)
				.defaultProject(false).build();
		return project;
	}
	
}
