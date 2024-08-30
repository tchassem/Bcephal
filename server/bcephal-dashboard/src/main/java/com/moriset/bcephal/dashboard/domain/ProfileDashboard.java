/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;


import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Entity(name = "ProfileDashboard")
@Table(name = "BCP_PROFILE_DASHBOARD")
@Data
@EqualsAndHashCode(callSuper = false)
public class ProfileDashboard extends Persistent {

	private static final long serialVersionUID = 1192859197216319781L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prifile_dashboard_seq")
	@SequenceGenerator(name = "prifile_dashboard_seq", sequenceName = "prifile_dashboard_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long profileId;
	
	private Long dashboardId;
	
	private int position;
	
	private boolean defaultDashboard;
	
	@Transient
	private String name;

	@Override
	public Persistent copy() {
		return null;
	}
	
}
