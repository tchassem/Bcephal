/**
 * 
 */
package com.moriset.bcephal.security.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.User;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface UserRepository extends MainObjectRepository<User> {
	
	Optional<User> findByUsernameIgnoreCase(String login);
	
	Optional<User> findByUsernameIgnoreCaseAndClientId(String login, Long clientId);
	Optional<User> findByUsernameAndClientId(String login, Long clientId);

	void deleteByClientId(Long clientId);

	List<User> findByClientId(Long clientId);
	
}
