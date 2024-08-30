package com.moriset.bcephal.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.security.domain.UniverseLocker;
import com.moriset.bcephal.security.repository.UniverseLockerRepository;

@Service
public class UniverseLockerService {

	@Autowired
	UniverseLockerRepository universeLockerRepository;

	public boolean lockProject(String projectCode, String user, String sessionId) {
		UniverseLocker universeLocker = new UniverseLocker();
		universeLocker.setProjectCode(projectCode);
		universeLocker.setSessionId(sessionId);
		universeLocker.setUser(user);
		universeLockerRepository.save(universeLocker);
		return true;
	}

	public boolean isProjectLocked(String projectCode) {
		UniverseLocker universeLocker = universeLockerRepository.findByProjectCode(projectCode);
		return universeLocker != null;
	}

	public boolean isUserLockProject(String projectCode, String user, String sessionId) {
		UniverseLocker universeLocker = universeLockerRepository.findByProjectCodeAndUserAndSessionId(projectCode, user,
				sessionId);
		return universeLocker != null;
	}

	public boolean isUserLockProject(String projectCode, String sessionId) {
		UniverseLocker universeLocker = universeLockerRepository.findByProjectCodeAndSessionId(projectCode, sessionId);
		return universeLocker != null;
	}

	public void realeasedProject(String projectCode) {
		universeLockerRepository.deleteByProjectCode(projectCode);
	}

	public void releasedProjectBySessionId(String sessionId) {
		universeLockerRepository.deleteBySessionId(sessionId);
	}

	public void realeasedProject(String projectCode, String sessionId) {
		if (sessionId == null) {
			realeasedProject(projectCode);
		} else if (isUserLockProject(projectCode, sessionId)) {
			realeasedProject(projectCode);
		}
	}

}
