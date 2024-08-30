/**
 * 
 */
package com.moriset.bcephal.security.domain;

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

	private List<Nameable> profiles;

	public ProfileDashboardEditorData() {
        this.itemListChangeHandler = new ListChangeHandler<ProfileDashboard>();
        this.profiles = new ArrayList<Nameable>();
    }
	
}
