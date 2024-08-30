package com.moriset.bcephal.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.UniverseLocker;

@Repository
public interface UniverseLockerRepository extends JpaRepository<UniverseLocker, Long> {

	UniverseLocker findByProjectCode(String projectCode);

	UniverseLocker findByProjectCodeAndUserAndSessionId(String projectCode, String user, String sessionId);

	void deleteByProjectCode(String projectCode);

	void deleteBySessionId(String sessionId);

	UniverseLocker findByProjectCodeAndSessionId(String projectCode, String sessionId);

}
