package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.FunctionalityBlock;

/**
 * 
 * @author MORISET-004
 *
 */
@Repository
public interface FunctionalityBlockRepository extends JpaRepository<FunctionalityBlock, Long> {

	List<FunctionalityBlock> findByUsername(String username);

	List<FunctionalityBlock> findByProjectIdAndUsername(Long projectId, String username);

	@Query(value = "DELETE FROM com.moriset.bcephal.security.domain.FunctionalityBlock block where block.groupId.id = :groupId")
	void deleteByGroupId(@Param("groupId")Long groupId);

}
