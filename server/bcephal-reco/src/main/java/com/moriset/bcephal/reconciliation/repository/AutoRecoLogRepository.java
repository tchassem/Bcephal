/**
 * 
 */
package com.moriset.bcephal.reconciliation.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLog;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author MORISET-004
 *
 */
@Repository
public interface AutoRecoLogRepository extends PersistentRepository<AutoRecoLog> {

//@Query("SELECT item FROM AutoRecoLog item WHERE recoId = :recoId AND item.status != :status")
//AutoRecoLog getCurrentLogByReco(Long recoId, String status);
@Query("SELECT item FROM AutoRecoLog item WHERE recoId = :recoId AND item.status != :status")
AutoRecoLog getCurrentLogByReco(@Param("recoId") Long recoId, @Param("status") RunStatus status);
	
}
