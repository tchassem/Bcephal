/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileDashboard;

/**
 * @author Moriset
 *
 */
public interface ProfileDashboardRepository extends PersistentRepository<ProfileDashboard> {

	List<ProfileDashboard> findByDashboardId(Long dashboardId);
	
	@Query("SELECT profile FROM com.moriset.bcephal.security.domain.ProfileDashboard pd "
			+ "LEFT JOIN com.moriset.bcephal.security.domain.Profile profile ON pd.profileId = profile.id "
			+ "WHERE pd.dashboardId = :dashboardId order by pd.defaultDashboard DESC")
	List<Profile> getProfileByDashboardId(@Param("dashboardId") Long dashboardId);

	
	
}
