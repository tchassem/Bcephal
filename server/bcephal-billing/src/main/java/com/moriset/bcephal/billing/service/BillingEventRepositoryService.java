/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.repository.InvoiceRepository;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service("BillingEventRepositoryService")
@Slf4j
public class BillingEventRepositoryService extends GrilleService {
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	InvoiceRepository invoiceRepository;

	@Override
	public EditorData<Grille> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Grille> data = new EditorData<>();
		data.setItem(getBillingEventRepository());		
		data.setModels(getInitiationService().getModels(session, locale));
		data.setPeriods(getInitiationService().getPeriods(session, locale));
		data.setMeasures(getInitiationService().getMeasures(session, locale));
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		return data;
	}
	
	public Grille getBillingEventRepository() {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_REPOSITORY_GRID, ParameterType.GRID);
		if(parameter != null && parameter.getLongValue() != null) {
			return getById(parameter.getLongValue());
		}
		return null;
	}
	
	@Override
	protected Grille getNewItem() {
		Grille grid = new Grille();
		grid.setType(GrilleType.BILLING_EVENT_REPOSITORY);
		String baseName = "Billing event repository ";
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
		return GrilleType.BILLING_EVENT_REPOSITORY;
	}
	
	@Override
	public void loadFilterClosures(GrilleDataFilter filter) {
//		if(StringUtils.hasText(filter.getCriteria())) {
//			Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
//			if(parameter != null && parameter.getLongValue() != null) {
//				AttributeFilterItem item = new AttributeFilterItem();
//				item.setDataSourceId(filter.getGrid().getId());
//				item.setDataSourceType(filter.getGrid().getDataSourceType());
//				item.setDimensionId(parameter.getLongValue());
//				item.setDimensionType(DimensionType.ATTRIBUTE);
//				item.setOperator(AttributeOperator.EQUALS);
//				item.setFilterVerb(FilterVerb.AND);
//				item.setValue(filter.getCriteria());
//				
//				filter.getGrid().setGridUserFilter(new UniverseFilter());
//				filter.getGrid().getGridUserFilter().setAttributeFilter(new AttributeFilter());				
//				filter.getGrid().getGridUserFilter().getAttributeFilter().addItem(item);
//				filter.setCriteria(null);
//			}
//		}
		if(filter.getGroupId() != null) {
			Optional<Invoice> result = invoiceRepository.findById(filter.getGroupId());
			if(result.isPresent()) {
				String ref = result.get().getReference();
				Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
				if(parameter != null && parameter.getLongValue() != null) {
					AttributeFilterItem item = new AttributeFilterItem();
					item.setDataSourceId(filter.getGrid().getId());
					item.setDataSourceType(filter.getGrid().getDataSourceType());
					item.setDimensionId(parameter.getLongValue());
					item.setDimensionType(DimensionType.ATTRIBUTE);
					item.setOperator(AttributeOperator.EQUALS);
					item.setFilterVerb(FilterVerb.AND);
					item.setValue(ref);
					
					filter.getGrid().setGridUserFilter(new UniverseFilter());
					filter.getGrid().getGridUserFilter().setAttributeFilter(new AttributeFilter());				
					filter.getGrid().getGridUserFilter().getAttributeFilter().addItem(item);
					filter.setCriteria(null);
				}
			}
		}
		super.loadFilterClosures(filter);
	}
	
	
	
	@Transactional
	@Override
	public int deleteRows(List<Long> ids, Locale locale) {
		try {	
			Long billingEventStatusId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if(billingEventStatusId == null) {
				throw new BcephalException("Unable to delete row(s) : Missing setting : Billing event satatus attribute");
			}
			String billingEventStatusBilledValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE);
			if(!StringUtils.hasText(billingEventStatusBilledValue)) {
				throw new BcephalException("Unable to delete row(s) : Missing setting : Billing event satatus BILLED value");
			}
			
			String sql = "SELECT COUNT(1) FROM ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
					.concat(" WHERE ").concat(UniverseParameters.ID).concat(" IN :ids AND ")
					.concat(new Attribute(billingEventStatusId).getUniverseTableColumnName())
					.concat(" = :billed");
			Query query = session.createNativeQuery(sql);
			query.setParameter("ids", ids);
			query.setParameter("billed", billingEventStatusBilledValue);
			Number count = (Number)query.getSingleResult();
			if(count.longValue() > 0) {
				throw new BcephalException("Unable to delete row(s) : Selection contains " + count + " billed row(s)");
			}
						
			sql = "DELETE FROM ".concat(UniverseParameters.UNIVERSE_TABLE_NAME)
					.concat(" WHERE ").concat(UniverseParameters.ID).concat(" IN :ids");
			query = session.createNativeQuery(sql);
			query.setParameter("ids", ids);
			count = query.executeUpdate();
			return count.intValue();
		} catch (BcephalException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to delete rows", ex);
			String message = getMessageSource().getMessage("unable.to.delete.rows", new Object[]{ids} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		} 
	}
	
	protected Long getParameterLongValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getLongValue() : null;
	}
	
	protected String getParameterStringValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getStringValue() : null;
	}

}
