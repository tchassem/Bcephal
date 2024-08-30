/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.FunctionalityBlockGroup;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface FunctionalityBlockGroupRepository extends JpaRepository<FunctionalityBlockGroup, Long> {

	List<FunctionalityBlockGroup> findByUsername(String username);

	List<FunctionalityBlockGroup> findByProjectIdAndUsername(Long projectId, String username);

}
