package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;
import com.moriset.bcephal.grid.domain.VariableReference;
import com.moriset.bcephal.grid.repository.VariableReferenceRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Service
@Data
@EqualsAndHashCode(callSuper = false)
public class VariableReferenceService extends PersistentService<VariableReference, BrowserData> {
	
	@Autowired
	VariableReferenceRepository repository;
	
	@Autowired
	GrilleService grilleService;
	
	
	public List<Object> getValues(VariableReference reference, DimensionType dimensionType, Map<String, Object> variableValues) throws Exception{
		if(reference != null && reference.getDataSourceType() != null && reference.getDataSourceId() != null
				&& reference.getSourceId() != null) {
			DimensionDataFilter filter = new DimensionDataFilter(reference, dimensionType);
			filter.setAllowRowCounting(false);
			filter.setShowAll(true);
			for(String var : variableValues.keySet()) {
				Object val = variableValues.get(var);
				if(val != null) {
					VariableValue value = new VariableValue();
					value.setName(var);
					value.setIgnoreCase(false);
					if(val instanceof BigDecimal) {
						value.setDecimalValue((BigDecimal)val);
					}
					else if(val instanceof String) {
						value.setStringValue((String)val);
					}
					else if(val instanceof Date) {
						value.setPeriodValue((Date)val);
					}
					else if(val instanceof VariableIntervalPeriod) {
						//value.setPeriodValue((VariableIntervalPeriod)val);
					}					
					filter.getVariableReference().getVariableValues().add(value);
				}
			}			
			
			return grilleService.searchAttributeValues(filter, Locale.ENGLISH).getItems();
		}
		return new ArrayList<>();
	}
	
	
	
	@Override
	public VariableReferenceRepository getRepository() {
		return repository;
	}

}
