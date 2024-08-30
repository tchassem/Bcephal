/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.service.GrilleService;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingParameterBuilder extends ParameterBuilder {

	protected String BILLING_MODEL_NAME = "Billing model";
	protected String BILLING_MEASURE_NAME = "Billing";
	protected String BILLING_PERIOD_NAME = "Billing";
	protected Measure billingMeasure;
	protected Model billingModel;
	protected Map<String, Entity> entities;

	protected String entityName;
	

	@Autowired
	protected GrilleService grilleService;

	public BillingParameterBuilder() {
		super();
		this.entities = new HashMap<String, Entity>();
	}

	@Override
	protected void saveParameters(HashMap<Parameter, IPersistent> parameters, Locale locale) {
//		if(this.billingModel != null) {
//			parameterService.getInitiationService().save(this.billingModel, locale);
//		}
//		else if(this.entities != null) {
//			for(com.moriset.bcephal.utils.domain.dimension.Entity entity : this.entities.values()) {
//				parameterService.getInitiationService().saveEntity(entity, locale);
//			}
//		}
		for (Parameter parameter : parameters.keySet()) {
			IPersistent persistent = parameters.get(parameter);
			if (parameter.isValue()) {
				parameter.setStringValue(persistent != null ? ((AttributeValue) persistent).getName() : null);
			} else {
				if (parameter.isMeasure()) {
					if (billingMeasure != null) {
						((Measure) persistent).setParent(billingMeasure);
					}
					// ((Measure)persistent).setPosition(measureCount++);
					initiationService.getMeasureRepository().save((Measure) persistent);
				} else if (parameter.isPeriod()) {
					// ((Period)persistent).setPosition(periodCount++);
					initiationService.getPeriodRepository().save((Period) persistent);
				}
				parameter.setLongValue(persistent != null ? (Long) persistent.getId() : null);
			}
			this.parameterRepository.save(parameter);
		}
	}
	
	protected Grille getGrid(String code) {
		Grille grid = null;
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, ParameterType.GRID);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Grille> result = grilleService.getRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				grid = result.get();
			}
		}
		return grid;
	}

	
	protected Measure buildBillingMeasure() {
		Measure measure = initiationService.getMeasureRepository().findByName(BILLING_MEASURE_NAME);
		if (measure == null) {
			measure = new Measure(null, BILLING_MEASURE_NAME);
			int position = initiationService.getMeasureRepository().findByParentIsNull().size();
			measure.setPosition(position);
			measure = initiationService.getMeasureRepository().save(measure);
		}
		return measure;
	}
	
	protected Period buildBillingPeriod() {
		Period period = initiationService.getPeriodRepository().findByName(BILLING_PERIOD_NAME);
		if (period == null) {
			period = new Period(null, BILLING_PERIOD_NAME);
			int position = initiationService.getPeriodRepository().findByParentIsNull().size();
			period.setPosition(position);
			period = initiationService.getPeriodRepository().save(period);
		}
		return period;
	}
	
}
