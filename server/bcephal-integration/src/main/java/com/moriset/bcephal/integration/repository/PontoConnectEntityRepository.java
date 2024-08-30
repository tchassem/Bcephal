package com.moriset.bcephal.integration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.repository.PersistentRepository;

@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public interface PontoConnectEntityRepository extends PersistentRepository<PontoConnectEntity> {

	List<PontoConnectEntity> findByName(String name);
	List<PontoConnectEntity> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean active, boolean scheduled);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.integration.domain.PontoConnectEntity model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.integration.domain.PontoConnectEntity model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
}
