/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseInvoiceRepositoryService extends GrilleService {
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	InvoiceService invoiceService;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public EditorData<Grille> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Grille> data = new EditorData<>();
		data.setItem(getInvoiceRepository());		
		data.setModels(getInitiationService().getModels(session, locale));
		data.setPeriods(getInitiationService().getPeriods(session, locale));
		data.setMeasures(getInitiationService().getMeasures(session, locale));
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		return data;
	}
	
	public Grille getInvoiceRepository() {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(getRepositoryCode(), ParameterType.GRID);
		if(parameter != null && parameter.getLongValue() != null) {
			return getById(parameter.getLongValue());
		}
		return null;
	}
	
	public String getFileCodeByUniverseId(Long universeId) {
		Long invoiceId = getInvoiceObjectId(universeId, Locale.ENGLISH);
		Invoice invoice = invoiceService.getById(invoiceId);
		return invoice != null ? invoice.getFile() : null;
	}
	
	public Long getInvoiceObjectId(Long universeId, Locale locale) {
		try {					
			String sql = "SELECT {0} FROM {1} WHERE {2} = ? LIMIT 1";			
			sql = MessageFormat.format(sql,
					UniverseParameters.SOURCE_ID, 
					UniverseParameters.UNIVERSE_TABLE_NAME, 
					UniverseParameters.ID);			
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter(1, universeId);
			Number objectId = (Number)query.getSingleResult();
			return objectId != null ? objectId.longValue() : null;
		} catch (Exception ex) {
			log.error("Unable get invoice Id : {}.", universeId, ex);			
			String message = MessageFormat.format("Unable get invoice Id : {0}.", universeId);
			throw new BcephalException(message);
		} 			
	}	
	@Override
	protected Grille getNewItem() {
		Grille grid = new Grille();
		grid.setType(getGrilleType());
		String baseName = getRepositoryBaseName();
		int i = 1;
		grid.setName(baseName + i);
		while(getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}
		return grid;
	}
	
	@Override
	protected GrilleType getGrilleType() {		
		return GrilleType.INVOICE_REPOSITORY;
	}
	
	protected abstract String getRepositoryBaseName();
	
	protected abstract String getRepositoryCode();

	

}
