/**
 * 
 */
package com.moriset.bcephal.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.BLabel;

/**
 * @author Moriset
 *
 */
@Repository
public interface BLabelRepository extends PersistentRepository<BLabel> {

	List<BLabel> findAllByLangOrderByCode(String lang);

	@Query(value = "SELECT label.code, label.value FROM com.moriset.bcephal.domain.BLabel label where label.lang = :lang AND label.code IN :codes")
	List<String[]> findAllLabels(@Param("codes")Set<String> codes, @Param("lang")String lang);

	@Query(value = "SELECT count(1) FROM com.moriset.bcephal.domain.BLabel label WHERE label.code = :code")
	Number countByCode(@Param("code")String code);
}
