/**
 * 
 */
package com.moriset.bcephal.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Component
@Data
@Slf4j
public class SpotEvaluator {

	@PersistenceContext
	EntityManager entityManager;
	
	
	public BigDecimal evaluate(Spot spot, List<UniverseFilter> filters) throws Exception {
		if(spot == null) {
			throw new BcephalException("Unable to evaluate null spot!");
		}
		log.debug("Try evaluate spot : {}", spot.getName());
		if(spot.getMeasureId() == null) {
			throw new BcephalException("Unable to evaluate spot : '" + spot.getName() + "'. The spot measure is not defined!");
		}
		try {			
			BigDecimal amount = BigDecimal.ZERO;
			log.debug("Spot : {}  - Build evaluation query...", spot.getName());		
			String sql = new SpotQueryBuilder(spot, filters).buildQuery();
			log.debug("Spot : {}  - Evaluation query : {}", spot.getName(), sql);
			
			Query query = entityManager.createNativeQuery(sql);			
			Number number = (Number)query.getSingleResult();
			if(number != null) {
				amount = new BigDecimal(number.doubleValue());
			}
			log.debug("Spot : {}  - Evaluation result : {}", spot.getName(), amount);
			return amount;
		} 
		catch (Exception e) {
			log.error("Spot : {}		Evaluation fail !", spot.getName(), e);			
			throw new BcephalException("Unable to evaluate spot : '" + spot.getName() + "'.", e);
		} finally {

		}
	}
	
}
