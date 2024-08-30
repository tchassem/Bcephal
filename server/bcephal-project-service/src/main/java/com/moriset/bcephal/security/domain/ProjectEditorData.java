/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;

/**
 * @author Joseph Wambo
 *
 */
public class ProjectEditorData extends EditorData<Project> {

	public List<SubscriptionBrowserData> clients;

	public ProjectEditorData() {
		super();
		clients = new ArrayList<>(0);
	}

}
