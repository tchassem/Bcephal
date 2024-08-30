package com.moriset.bcephal.billing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * 
 * @author ROLAND
 *
 * @param <P>
 */

@Repository
public interface BillTemplateRepository extends MainObjectRepository<BillTemplate> {
	
	BillTemplate findByCode(String code);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(template.id, template.name, template.code) FROM com.moriset.bcephal.billing.domain.BillTemplate template ORDER BY name")
	List<Nameable> findAllAsNameables();
}
