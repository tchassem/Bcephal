/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.settings.domain.ParameterEditorData;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class ParameterService extends PersistentService<Parameter, BrowserData> {

	@Autowired
	ParameterRepository parameterRepository;

	@Autowired
	InitiationService initiationService;

	@Autowired
	IncrementalNumberService incrementalNumberService;

	@Autowired
	BillingParameterRoleBuilder billingParameterRoleBuilder;

	@Autowired
	BillingParameterEventBuilder billingParameterEventBuilder;

	@Autowired
	BillingParameterInvoiceBuilder billingParameterInvoiceBuilder;

	@Autowired
	InitiationParameterBuilder initiationParameterBuilder;

	@Autowired
	ReconciliationParameterBuilder reconciliationParameterBuilder;
	
	@Autowired
	AccountingParameterBuilder accountingParameterBuilder;
	
	@Autowired
	LogsParameterBuilder logsParameterBuilder;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	MaterializedGridService materializedGridService;

	@Override
	public ParameterRepository getRepository() {
		return parameterRepository;
	}

	public ParameterEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		log.debug("Call of get editor data");
		ParameterEditorData data = new ParameterEditorData();
		data.setItem(new Parameter());
		List<Parameter> parameters = getParameterRepository().findAll();
		data.getItem().setParameters(new ListChangeHandler<>(parameters));

		data.setModels(initiationService.getModels(session, locale));
		data.setPeriods(initiationService.getPeriods(session, locale));
		data.setMeasures(initiationService.getMeasures(session, locale));
		data.setGrids(grilleService.getRepository().findGenericAllAsNameables());
	    data.setMatGrids(materializedGridService.getRepository().findAllAsNameables());
		data.setSequences(incrementalNumberService.getIncrementalNumberRepository().getAllIncrementalNumbers());
		data.setParameterGroups(new ParameterGroupBuilder().buildParameterGroup());
//		data.setBilltemplates(billtemplates);
		return data;
	}

	public EditorData<Parameter> buildAutomatically(String code, HttpSession session, Locale locale) throws Exception {
		if ("billing.role".equals(code) || code.startsWith("billing.role")) {
			billingParameterRoleBuilder.buildParameters(session, locale);
		} else if ("billing.event".equals(code)) {
			billingParameterEventBuilder.buildParameters(session, locale);
		} else if ("billing.invoice".equals(code)) {
			billingParameterInvoiceBuilder.buildParameters(session, locale);
		} else if ("initiation".equals(code)) {
			initiationParameterBuilder.buildParameters(session, locale);
		} else if ("reconciliation".equals(code)) {
			reconciliationParameterBuilder.buildParameters(session, locale);
		} else if ("accounting".equals(code)) {
			accountingParameterBuilder.buildParameters(session, locale);
		} else if ("logs".equals(code)) {
			logsParameterBuilder.buildParameters(session, locale);
		}
		
		return getEditorData(null, session, locale);
	}

	/**
	 * @param parameterCode
	 * @return
	 */
	protected Measure getMeasure(String parameterCode) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.MEASURE);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Measure> result = getInitiationService().getMeasureRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				return result.get();
			}
		}
		return null;
	}

	/**
	 * @param parameterCode
	 * @return
	 */
	protected Period getPeriod(String parameterCode) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.PERIOD);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Period> result = getInitiationService().getPeriodRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				return result.get();
			}
		}
		return null;
	}

	/**
	 * @param parameterCode
	 * @return
	 */
	public Entity getEntity(String parameterCode) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.ENTITY);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Entity> result = getInitiationService().getEntityRepository().findById(parameter.getLongValue());
			if (result.isPresent()) {
				return result.get();
			}
		}
		return null;
	}

	/**
	 * @param parameterCode
	 * @return
	 */
	public Attribute getAttribute(String parameterCode) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode, ParameterType.ATTRIBUTE);
		if (parameter != null && parameter.getLongValue() != null) {
			Optional<Attribute> result = getInitiationService().getAttributeRepository()
					.findById(parameter.getLongValue());
			if (result.isPresent()) {
				return result.get();
			}
		}
		return null;
	}

	/**
	 * @param parameterCode
	 * @return
	 */
	public AttributeValue getAttributeValue(String parameterCode) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(parameterCode,
				ParameterType.ATTRIBUTE_VALUE);
		if (parameter != null) {
			if (parameter.getLongValue() != null) {
				Optional<AttributeValue> result = getInitiationService().getAttributeValueRepository()
						.findById(parameter.getLongValue());
				if (result.isPresent()) {
					return result.get();
				}
			}
			if (parameter.getStringValue() != null) {
				AttributeValue value = new AttributeValue();
				value.setName(parameter.getStringValue());
				return value;
			}
		}
		return null;
	}

	@Transactional
	public Parameter saveRoot(Parameter parameter, Locale locale) {
		log.debug("Try to  Save parameters ");
		try {
			if (parameter == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.parameter", new Object[] {},
						locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}

			ListChangeHandler<Parameter> parameters = parameter.getParameters();
			parameters.getNewItems().forEach(item -> {
				log.trace("Try to save Parameter : {}", item);
				parameterRepository.save(item);
				log.trace("Parameter saved : {}", item.getId());
			});
			parameters.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Parameter : {}", item);
				parameterRepository.save(item);
				log.trace("Parameter saved : {}", item.getId());
			});
			parameters.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Parameter : {}", item);
					parameterRepository.deleteById(item.getId());
					log.trace("Parameter deleted : {}", item.getId());
				}
			});
			log.debug("Parameters saved ");
			return parameter;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save parameters", e);
			String message = getMessageSource().getMessage("unable.to.save.parameters", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
