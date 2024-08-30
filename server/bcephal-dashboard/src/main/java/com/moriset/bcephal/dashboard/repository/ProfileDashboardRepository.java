/**
 * 
 */
package com.moriset.bcephal.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.dashboard.domain.ProfileDashboard;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Moriset
 *
 */
public interface ProfileDashboardRepository extends PersistentRepository<ProfileDashboard> {

	List<ProfileDashboard> findByProfileId(Long profileId);
	
	List<ProfileDashboard>  findByDashboardId(Long dashboardId);
	
	@Query("SELECT dashboard FROM com.moriset.bcephal.dashboard.domain.ProfileDashboard pd "
			+ "LEFT JOIN com.moriset.bcephal.dashboard.domain.Dashboard dashboard ON pd.dashboardId = dashboard.id "
			+ "WHERE pd.profileId = :profileId order by dashboard.name ASC, pd.defaultDashboard DESC")
	List<Dashboard> getDashboardByProfileId(@Param("profileId") Long profileId);
	
	@Query("SELECT pd FROM com.moriset.bcephal.dashboard.domain.ProfileDashboard pd "
			+ "WHERE pd.profileId = :profileId")
	List<ProfileDashboard> getProfileDashboardByProfileId(@Param("profileId") Long profileId);

	
	
}
