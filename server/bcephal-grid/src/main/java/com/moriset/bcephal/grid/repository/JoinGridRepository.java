package com.moriset.bcephal.grid.repository;

import java.util.Optional;

import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.repository.PersistentRepository;

public interface JoinGridRepository extends PersistentRepository<JoinGrid> {

	Optional<JoinGrid> findByGridIdAndGridTypeAndJoinId(Long gridId, JoinGridType gridType, Join joindId);
	
}
