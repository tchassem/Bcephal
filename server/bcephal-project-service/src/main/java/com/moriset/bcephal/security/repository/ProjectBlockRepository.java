/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.ProjectBlock;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ProjectBlockRepository extends JpaRepository<ProjectBlock, Long> {

	public List<ProjectBlock> findByUsername(String username);

	public List<ProjectBlock> findBySubcriptionIdAndUsername(Long subscriptionId, String username);

	public int deleteByProjectId(Long projectId);

	public int deleteByCode(String code);

	public ProjectBlock findBySubcriptionIdAndProjectId(Long subscriptionId, Long projectId);

}
