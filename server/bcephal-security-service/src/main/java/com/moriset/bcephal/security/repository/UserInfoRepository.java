package com.moriset.bcephal.security.repository;

import java.util.Optional;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.UserInfo;

public interface UserInfoRepository extends PersistentRepository<UserInfo> {
	
	Optional<UserInfo> findByUsernameIgnoreCase(String login);
	Optional<UserInfo> findByUsername(String login);
	
	Optional<UserInfo> findByUsernameAndClientId(String login, Long clientId);
	
}