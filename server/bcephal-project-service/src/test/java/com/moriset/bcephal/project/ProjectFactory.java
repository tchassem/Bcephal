package com.moriset.bcephal.project;

import java.sql.Timestamp;

import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.domain.ProjectProfile;
import com.moriset.bcephal.security.domain.Subscription;
import com.moriset.bcephal.security.domain.Subscription.SubscriptionStatus;

public class ProjectFactory {

	
	public Subscription buildSubscription() {
		Subscription subscription = Subscription.builder()
				.name("Subcription" + System.currentTimeMillis())
				.defaultSubscription(false)
				.maxUser(100)
				.ownerUser("B-Cephal")
				.status(SubscriptionStatus.ACTIVE)
				.build();
		return subscription;
	}
	
	public Project buildProject() {
		Project project = Project.builder()
				.code("" + System.currentTimeMillis())
				.subscriptionId(1L)
				.creationDate(new Timestamp(System.currentTimeMillis()))
				.build();
		project.setName(String.format("Project %s", project.getCode()));
		project.setDescription(project. toString());
		return project;
	}
	
	public ProjectProfile buildProjectProfile() {
		ProjectProfile projectProfile = new ProjectProfile();
		projectProfile.setProfileId(1L);
		projectProfile.setClientId(System.currentTimeMillis());
		return projectProfile;
	}
	
	public ProjectBrowserData buildProjectBrowserData() {
		ProjectBrowserData projectBrowserData = ProjectBrowserData.builder()
				.code("" + System.currentTimeMillis())
				.subscriptionId(1L)
				.creationDate(new Timestamp(System.currentTimeMillis()))
				.build();
		projectBrowserData.setName(String.format("Project %s", projectBrowserData.getCode()));
		return projectBrowserData;
	}

}