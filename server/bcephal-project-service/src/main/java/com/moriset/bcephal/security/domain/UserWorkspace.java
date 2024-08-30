/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class UserWorkspace {

	private List<SubscriptionBrowserData> clients;
	private SubscriptionBrowserData defaultClient;

	private List<ProjectBrowserData> availableProjects;
	private List<ProjectBrowserData> defaultProjects;

	public UserWorkspace() {
		clients = new ArrayList<SubscriptionBrowserData>(0);
		availableProjects = new ArrayList<ProjectBrowserData>(0);
		defaultProjects = new ArrayList<ProjectBrowserData>(0);
	}

}
