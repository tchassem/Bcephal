package com.moriset.bcephal.alarm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.domain.AlarmAttachment;
import com.moriset.bcephal.alarm.domain.AlarmAudience;
import com.moriset.bcephal.alarm.domain.AlarmCategory;
import com.moriset.bcephal.alarm.domain.AlarmEditorData;
import com.moriset.bcephal.alarm.repository.AlarmAttachmentRepository;
import com.moriset.bcephal.alarm.repository.AlarmAudienceRepository;
import com.moriset.bcephal.alarm.repository.AlarmRepository;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Entity;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.repository.SmartGrilleRepository;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.condition.ConditionalExpressionService;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlarmService extends MainObjectService<Alarm, BrowserData>{

	@Autowired
	AlarmRepository alarmRepository;
	
	@Autowired
	UniverseFilterService universeFilterService;
	
	@Autowired
	AlarmAttachmentRepository attachmentRepository;
	
	@Autowired
	AlarmAudienceRepository audienceRepository;
	
	@Autowired
	ConditionalExpressionService conditionalExpressionService;
	
	@Autowired
	AlarmRunner alarmRunner;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Autowired
	SmartGrilleRepository gridRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	JoinService joinService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DASHBOARDING_ALARM;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	@Override
	public MainObjectRepository<Alarm> getRepository() {
		return alarmRepository;
	}
	
	public void sendMessage(Alarm alarm, String username, RunModes mode, String projectCode, Long client) {
		log.debug("Try to send alarm : {}", alarm.getName());
		try {
			alarmRunner.sendAlarm(alarm, username, mode, projectCode, client);
		} 
		catch (BcephalException e) {
			throw e;
		} 
		catch (Exception e) {
			log.error("Unexpected error while seding alarm : {}", alarm, e);
			String message = "Unable to send alarm";
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public Alarm save(Alarm alarm, Locale locale) {
		log.debug("Try to  Save alarm : {}", alarm);
		try {
			if (alarm == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.alarm", new Object[] { alarm },
						locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(alarm.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.alarm.with.empty.name",
						new String[] { alarm.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<AlarmAudience> audiences = alarm.getAudienceListChangeHandler();
			ListChangeHandler<AlarmAttachment> attachments = alarm.getAttachmentListChangeHandler();
			if (alarm.getCondition() != null) {
				conditionalExpressionService.save(alarm.getCondition(), locale);
			}
			if (alarm.getFilter() != null) {
				var filter = universeFilterService.save(alarm.getFilter());
				alarm.setFilter(filter);
			}
			alarm = super.save(alarm, locale);
			Alarm id = alarm;
			
			audiences.getNewItems().forEach(item -> {
				item.setAlarm(id);
				audienceRepository.save(item);
			});
			audiences.getUpdatedItems().forEach(item -> {
				item.setAlarm(id);
				audienceRepository.save(item);
			});
			audiences.getDeletedItems().forEach(item -> {
				if(item.getId() != null) {
					audienceRepository.deleteById(item.getId());
				}
			});
			
			attachments.getNewItems().forEach(item -> {
				item.setAlarm(id);
				attachmentRepository.save(item);
			});
			attachments.getUpdatedItems().forEach(item -> {
				item.setAlarm(id);
				attachmentRepository.save(item);
			});
			attachments.getDeletedItems().forEach(item -> {
				if(item.getId() != null) {
					attachmentRepository.deleteById(item.getId());
				}
			});			
						
			log.debug("Alarm successfully to save : {} ", alarm);		
			
			return alarm;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save alarm : {}", alarm, e);
			String message = getMessageSource().getMessage("unable.to.save.alarm", new Object[] { alarm }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public void delete(Alarm alarm) {
		if (alarm == null || alarm.getId() == null) {
			return;
		}
		ListChangeHandler<AlarmAudience> audiences = alarm.getAudienceListChangeHandler();
		ListChangeHandler<AlarmAttachment> attachments = alarm.getAttachmentListChangeHandler();
		audiences.getItems().forEach(item -> {
			if(item.getId() != null) {
				audienceRepository.deleteById(item.getId());
			}
		});
		audiences.getOriginalList().forEach(item -> {
			if(item.getId() != null) {
				audienceRepository.deleteById(item.getId());
			}
		});
		audiences.getDeletedItems().forEach(item -> {
			if(item.getId() != null) {
				audienceRepository.deleteById(item.getId());
			}
		});
		
		attachments.getItems().forEach(item -> {
			if(item.getId() != null) {
				attachmentRepository.deleteById(item.getId());
			}
		});
		attachments.getOriginalList().forEach(item -> {
			if(item.getId() != null) {
				attachmentRepository.deleteById(item.getId());
			}
		});
		attachments.getDeletedItems().forEach(item -> {
			if(item.getId() != null) {
				attachmentRepository.deleteById(item.getId());
			}
		});
		
		if (alarm.getCondition() != null) {
			conditionalExpressionService.delete(alarm.getCondition());
		}
		
		getRepository().deleteById(alarm.getId());
		log.debug("Alarm successfully to delete : {} ", alarm);
	}
	
	@Override
	public EditorData<Alarm> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		EditorData<Alarm> base = super.getEditorData(filter, session, locale);
		AlarmEditorData data = new AlarmEditorData(base);
		data.variables = new AlarmVariables().getAllGroupByCategory();
		data.grids = gridRepository.findByType(GrilleType.REPORT);
		//initEditorDataForJoin(data, session, locale);
		return data;
	}
	
	protected void initEditorDataForJoin(AlarmEditorData data, HttpSession session, Locale locale) throws Exception {		
		List<Model> models = new ArrayList<>(0);
		List<Measure> measures = new ArrayList<>(0);
		List<Period> periods = new ArrayList<>(0);
		
		Join grid = null;
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_JOIN, ParameterType.JOIN);
		if(parameter != null && parameter.getLongValue() != null) {
			grid =  joinService.getById(parameter.getLongValue());
		}
		if(grid != null) {
			Model model = new Model();
			models.add(model);
			model.setName(grid.getName());
			Entity entity = new Entity();
			entity.setName("Columns");
			model.getEntities().add(entity);

			for (JoinColumn column : grid.getColumns()) {
				if (column.getType() == DimensionType.MEASURE) {
					measures.add(new Measure(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));						
				} else if (column.getType() == DimensionType.ATTRIBUTE) {						
					entity.getAttributes().add(new Attribute(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
				} else if (column.getType() == DimensionType.PERIOD) {
					periods.add(new Period(column.getId(), column.getName(), DataSourceType.JOIN, grid.getId()));
				}
			}

			Comparator<Dimension> comparator = new Comparator<Dimension>() {
				@Override
				public int compare(Dimension data1, Dimension data2) {
					return data1.getName().compareTo(data2.getName());
				}
			};

			if (models.get(0).getEntities().size() > 0) {
				Collections.sort(models.get(0).getEntities().get(0).getAttributes(), comparator);
			}
			Collections.sort(measures, comparator);
			Collections.sort(periods, comparator);
		}
		data.setModels(models);
		data.setMeasures(measures);
		data.setPeriods(periods);
	}

	@Override
	protected void initEditorData(EditorData<Alarm> data, HttpSession session, Locale locale) throws Exception {
		//data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		super.initEditorData(data, session, locale);
	}
	

	@Override
	protected Alarm getNewItem() {		
		Alarm alarm = new Alarm();
		String baseName = "Alarm ";
		int i = 1;
		alarm.setName(baseName + i);
		while (getByName(alarm.getName()) != null) {
			i++;
			alarm.setName(baseName + i);
		}		
		return alarm;
	}

	@Override
	protected BrowserData getNewBrowserData(Alarm alam) {
		return new BrowserData(alam);
	}

	@Override
	protected Specification<Alarm> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Alarm> qBuilder = new RequestQueryBuilder<Alarm>(root, query, cb);
			qBuilder.select(Alarm.class);			
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter != null && StringUtils.hasText(filter.getReportType())) {
				AlarmCategory category = AlarmCategory.valueOf(filter.getReportType());
				qBuilder.addEquals("category", category);
			}
			qBuilder.addNoTInObjectId(hidedObjectIds);
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

}
