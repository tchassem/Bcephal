/**
 * 
 */
package com.moriset.bcephal.accounting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.accounting.domain.Account;
import com.moriset.bcephal.accounting.domain.Posting;
import com.moriset.bcephal.accounting.domain.PostingBrowserData;
import com.moriset.bcephal.accounting.domain.PostingEditorData;
import com.moriset.bcephal.accounting.domain.PostingEntry;
import com.moriset.bcephal.accounting.domain.PostingStatus;
import com.moriset.bcephal.accounting.repository.PostingEntryRepository;
import com.moriset.bcephal.accounting.repository.PostingRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class PostingService extends MainObjectService<Posting, PostingBrowserData> {
	
	@Autowired
	PostingRepository postingRepository;
	
	@Autowired
	PostingEntryRepository postingEntryRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.ACCOUNTING_POSTING;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode,String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
	
	@Override
	public PostingRepository getRepository() {
		return postingRepository;
	}
	
	@Override
	public EditorData<Posting> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {		
		EditorData<Posting> editorData = super.getEditorData(filter, session, locale);		
		PostingEditorData data = new PostingEditorData();
		data.setItem(editorData.getItem());
		data.setCalendarCategories(editorData.getCalendarCategories());
		data.setMeasures(editorData.getMeasures());
		data.setModels(editorData.getModels());
		data.setPeriods(editorData.getPeriods());
		data.setSpots(editorData.getSpots());
		data.accounts.add(new Account("156445233625", "Produits"));
		data.accounts.add(new Account("254585558452", "Charges"));
		data.accounts.add(new Account("478452664569", "Ventilation"));
		return data;		
	}

	protected Posting getNewItem(String baseName, boolean startWithOne) {
		Posting config = new Posting();
		int i = 0;
		config.setName(baseName);
		if(startWithOne) {
			i = 1;
			config.setName(baseName + i);
		}
		while(getByName(config.getName()) != null) {
			i++;
			config.setName(baseName + i);
		}
		return config;
	}
	
	@Override
	protected Posting getNewItem() {
		return getNewItem("Posting ", false);
	}

	@Override
	protected PostingBrowserData getNewBrowserData(Posting item) {
		return new PostingBrowserData(item);
	}

	@Override
	protected Specification<Posting> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Posting> qBuilder = new RequestQueryBuilder<Posting>(root, query, cb);
			qBuilder.select(BrowserData.class);		   
		    if (filter != null && !org.apache.commons.lang3.StringUtils.isBlank(filter.getCriteria())) {
		    	qBuilder.addLikeCriteria("name", filter.getCriteria());
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

	protected void build(ColumnFilter columnFilter) {		
		if ("Status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("entryDate");
			columnFilter.setType(PostingStatus.class);
		} else if ("EntryDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("entryDate");
			columnFilter.setType(Date.class);
		} else if ("Username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("Balance".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("balance");
			columnFilter.setType(BigDecimal.class);
		} else if ("ValueDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("balance");
			columnFilter.setType(Date.class);
		}
		super.build(columnFilter);
	}

	
	@Override
	@Transactional
	public Posting save(Posting posting, Locale locale) {
		log.debug("Try to Posting Entry : {}", posting);		
		try {	
			if(posting == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.grid", new Object[]{posting} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if(!StringUtils.hasLength(posting.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.booking.model.with.empty.name", new String[]{posting.getName()} , locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}			
			 ListChangeHandler<PostingEntry> items = posting.getEntryListChangeHandler();
			
			posting.setModificationDate(new Timestamp(System.currentTimeMillis()));
			posting = postingRepository.save(posting);
			Posting id = posting;
			items.getNewItems().forEach( item -> {
				log.trace("Try to save posting Entry : {}", item);
				item.setPosting(id);
				postingEntryRepository.save(item);
				log.trace("Posting Entry  saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach( item -> {
				log.trace("Try to save   posting Entry  : {}", item);
				item.setPosting(id);
				postingEntryRepository.save(item);
				log.trace("Posting Entry  saved : {}", item.getId());
			});
			items.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete  posting Entry  : {}", item);
					postingEntryRepository.deleteById(item.getId());
					log.trace("Posting Entry  deleted : {}", item.getId());
				}
			});
			
			log.debug("Posting  saved : {} ", posting.getId());
	        return posting;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save Posting  : {}", posting, e);
			String message = getMessageSource().getMessage("unable.to.save.posting.entry", new Object[]{posting} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	@Override
	@Transactional
	public void delete(Posting posting) {
		log.debug("Try to delete Posting : {}", posting);	
		if(posting == null || posting.getId() == null) {
			return;
		}		
		ListChangeHandler<PostingEntry> items = posting.getEntryListChangeHandler();
		items.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete Posting Entry : {}", item);
				postingEntryRepository.deleteById(item.getId());
				log.trace("Posting Entry deleted : {}", item.getId());
			}
		});
		postingRepository.deleteById(posting.getId());	
		
		log.debug("Posting successfully to delete : {} ", posting);
	    return;	
	}
	
	public Posting validate(Posting posting, Locale locale) {
		if(posting != null) {
			if(!posting.getStatus().isValidated()) {						
				posting.setStatus(PostingStatus.VALIDATED);
				posting = save(posting, locale);
			}
		}
		return posting;
	}
	
	public int validate(List<Long> ids, Locale locale) {
		int count = 0;
		for(Long id : ids) {
			if(id != null) {
				Posting posting = getById(id);	
				if(posting != null) {
					if(posting.getStatus().isValidated()) {
						continue;
					}
					else {		
						posting.setStatus(PostingStatus.VALIDATED);
						posting = save(posting, locale);
						++count;
					}
				}
			}
		}		
		return count;
	}
	
	public Posting resetValidation(Posting posting, Locale locale) {
		if(posting != null) {
			posting.setStatus(PostingStatus.DRAFT);
			posting = save(posting, locale);
		}
		return posting;
	}
	
	public int resetValidation(List<Long> ids, Locale locale) {
		int count = 0;
		for(Long id : ids) {
			if(id != null) {
				Posting posting = getById(id);	
				if(posting != null) {
					if(posting.getStatus().isDraft()) {
						continue;
					}
					else {		
						posting.setStatus(PostingStatus.DRAFT);
						posting = save(posting, locale);
						++count;
					}
				}
			}
		}		
		return count;
	}
	
	
	
}
