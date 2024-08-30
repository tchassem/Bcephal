package com.moriset.bcephal.billing.service.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

public class ClientItemReader extends AbstractPagingItemReader<String> {
	
	private EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;

	private final Map<String, Object> jpaPropertyMap = new HashMap<>();

	private String queryString;

	private Map<String, Object> parameterValues;
	
	private boolean transacted = true;//default value

	public ClientItemReader() {
		setName(ClassUtils.getShortName(ClientItemReader.class));
		
	}

	/**
	 * Create a query using an appropriate query provider (entityManager OR
	 * queryProvider).
	 */
	private Query createQuery() {
		return entityManager.createNativeQuery(queryString);
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * The parameter values to be used for the query execution.
	 *
	 * @param parameterValues the values keyed by the parameter named used in
	 * the query string.
	 */
	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}
	
	/**
	 * By default (true) the EntityTransaction will be started and committed around the read.  
	 * Can be overridden (false) in cases where the JPA implementation doesn't support a 
	 * particular transaction.  (e.g. Hibernate with a JTA transaction).  NOTE: may cause 
	 * problems in guaranteeing the object consistency in the EntityManagerFactory.
	 * 
	 * @param transacted indicator
	 */
	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}	

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(entityManagerFactory, "EntityManager is required when queryProvider is null");
		Assert.hasLength(queryString, "Query string is required when queryProvider is null");
	}

	/**
	 * @param queryString JPQL query string
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	protected void doOpen() throws Exception {
		super.doOpen();
		entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap);
		if (entityManager == null) {
			throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doReadPage() {

		EntityTransaction tx = null;		
		if (transacted) {
			tx = entityManager.getTransaction();
			tx.begin();
			
			entityManager.flush();
			entityManager.clear();
		}//end if

		Query query = createQuery().setFirstResult(getPage() * getPageSize()).setMaxResults(getPageSize());

		if (parameterValues != null) {
			for (Map.Entry<String, Object> me : parameterValues.entrySet()) {
				query.setParameter(me.getKey(), me.getValue());
			}
		}

		if (results == null) {
			results = new CopyOnWriteArrayList<>();
		}
		else {
			results.clear();
		}
		
		if (!transacted) {
			List<String> queryResult = query.getResultList();
			for (String entity : queryResult) {
				entityManager.detach(entity);
				results.add(entity);
			}
		} 
		else {
			results.addAll(query.getResultList());
			tx.commit();
		}
	}


	@Override
	protected void doClose() throws Exception {
		entityManager.close();
		super.doClose();
	}
	
}
