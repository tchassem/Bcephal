/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.ProfileData;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.domain.UserData;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.ProfileUserDataRepository;
import com.moriset.bcephal.security.repository.RightDataRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;

/**
 * @author Moriset
 *
 */
@Service
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class SecurityService {
	
	@Autowired
	UserDataRepository userRepository;
	
	@Autowired
	ProfileDataRepository profileRepository;
	
	@Autowired
	ProfileUserDataRepository profileUserRepository;
	
	@Autowired
	RightDataRepository rightDataRepository;
	
	public UserData getUserById(Long id) {
		Optional<UserData> item = userRepository.findById(id);
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}
	
	public ProfileData getProfileById(Long id) {
		Optional<ProfileData> item = profileRepository.findById(id);
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}
	
	public List<UserData> getUsersByProfileId(Long profilid) {
		List<UserData> items = profileUserRepository.findUsersByProfileId(profilid);		
		return items;
	}
	
	public List<Long> getHideProfileById(Long profileId, String functionality, String projectCode) {
		ProfileData profile = getProfileById(profileId);
		if(profile != null && profile.getType() != null && (profile.getType().isAdministratorOrSuperUser())) {			
			return new ArrayList<>();
		}
		Optional<List<Long>> item = rightDataRepository.findByProfileIdAndLevel(profileId, RightLevel.NONE, functionality, projectCode);
		if (item.isPresent()) {
			return item.get();
		}
		return new ArrayList<>();
	}

}
