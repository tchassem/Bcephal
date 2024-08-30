/**
 * 4 avr. 2024 - WriteOffUnionService.java
 *
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffUnionService {

	private EntityManager entityManager;
	
	private String username;
	
	public void writeOff(ReconciliationUnionData data, String recoNumber, Date recoDate, String username, RunModes mode) throws Exception {
		log.debug("Try to edit writeoff");
		if (data == null || data.getWriteOffAmount() == null || data.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		String sql = data.buildWriteOffSql(recoNumber, recoDate, username, mode);
		log.trace("WO query : {}", sql);	
		Query query = entityManager.createNativeQuery(sql);
		int n = 1;
		for(Object parameter : data.getParameters()){
			query.setParameter(n++, parameter);
		}
		query.executeUpdate();	
	}
	
	
	protected String[] addToQuery(String colName, Object value, String coma, String stringQuery, String valueQuery, List<Object> parameters) {
		if (value != null) {
			stringQuery += coma + colName;			
			valueQuery += coma + "?";
			parameters.add(value);
		}
		return new String[] {stringQuery, valueQuery};
	}

}
