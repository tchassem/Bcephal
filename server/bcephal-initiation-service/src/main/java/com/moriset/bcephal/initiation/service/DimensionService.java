package com.moriset.bcephal.initiation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.initiation.domain.Dimension;
import com.moriset.bcephal.initiation.repository.DimensionRepository;
import com.moriset.bcephal.service.PersistentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public abstract class DimensionService<P extends Dimension> extends PersistentService<P, BrowserData> {

	@Autowired
	UniverseGenerator universeGenerator;
	
	@PersistenceContext
	EntityManager session;

	public abstract DimensionRepository<P> getRepository();

	public P getByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}
	
	public P getByNameIgnoreCase(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByNameIgnoreCase(name);
	}

//	public P getByParent(Long parent) {
//		log.debug("Try to  get by parent : {}", parent);
//		if (getRepository() == null) {
//			return null;
//		}
//		return getRepository().findByParent(parent);
//	}
	
	public boolean canDeleteDimension(String col) 
	{
		String countsql = "SELECT COUNT(1) FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE " + col
				+ " IS NOT NULL";
		Query query = this.session.createNativeQuery(countsql);
		Number number = (Number) query.getSingleResult();		
		return number.intValue() <= 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDimensionDatasources(String col) 
	{
		String sql = "SELECT Distinct " + UniverseParameters.SOURCE_TYPE + " || ' : ' || " + UniverseParameters.SOURCE_NAME + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME + " WHERE " + col
				+ " IS NOT NULL LIMIT 5";
		Query query = this.session.createNativeQuery(sql);
		return query.getResultList();	
	}
	
}
