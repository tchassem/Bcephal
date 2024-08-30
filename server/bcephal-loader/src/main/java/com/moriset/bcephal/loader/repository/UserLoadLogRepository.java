package com.moriset.bcephal.loader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.loader.domain.UserLoadLog;
import com.moriset.bcephal.repository.PersistentRepository;

public interface UserLoadLogRepository extends PersistentRepository<UserLoadLog> {

	List<UserLoadLog> findAllByLoadId(Long loadId);

	@Query(value = "SELECT COUNT(log) FROM BCP_USER_LOAD_LOG log LEFT JOIN BCP_USER_LOAD load ON (load.id = log.loadId) WHERE log.file = :file AND load.loaderId = :userLoaderId", nativeQuery = true)
	Number countByLoaderIdAndFile(@Param("userLoaderId")Long userLoaderId, @Param("file")String file);

}
