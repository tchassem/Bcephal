/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class ProfileDashboardEditorData {

	private ListChangeHandler<ProfileDashboard> itemListChangeHandler;

	private List<Nameable> dashboards;

	public ProfileDashboardEditorData() {
        this.itemListChangeHandler = new ListChangeHandler<ProfileDashboard>();
        this.dashboards = new ArrayList<Nameable>();
    }
	
}
