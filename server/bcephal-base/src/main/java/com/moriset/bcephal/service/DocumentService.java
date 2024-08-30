package com.moriset.bcephal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.Document;
import com.moriset.bcephal.domain.DocumentBrowserData;
import com.moriset.bcephal.domain.GroupBrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

@Service
public class DocumentService extends MainObjectService<Document, DocumentBrowserData> {

	@Autowired
	DocumentRepository documentRepository;
	
	@Override
	public DocumentRepository getRepository() {
		return documentRepository;
	}
	
	
	@Override
	protected DocumentBrowserData getNewBrowserData(Document item) {
		return new DocumentBrowserData(item);
	}
	
	@Override
	protected void validateBeforeSave(Document document, Locale locale) {
		List<Document> objects = getAllByName(document);
		for(Document obj : objects) {
			if(!obj.getId().equals(document.getId())) {
				String message = messageSource.getMessage("duplicate.name", new Object[] { document.getName() },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
		}
	}
	
	public List<Document> getAllByName(Document document) {
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findBySubjectTypeAndSubjectIdAndName(document.getSubjectType(), document.getSubjectId(), document.getName());
	}

	@Override
	protected Specification<Document> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {

		return (root, query, cb) -> {
			RequestQueryBuilder<Document> qBuilder = new RequestQueryBuilder<Document>(root, query, cb);
			qBuilder.select(GroupBrowserData.class, root.get("id"), root.get("name"), root.get("visibleInShortcut"),
					root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter != null && StringUtils.hasText(filter.getSubjectType())) {
				qBuilder.addEquals("subjectType", filter.getSubjectType());
				if(filter.getSubjectId() != null) {
					qBuilder.addEqualsCriteria("subjectId", filter.getSubjectId());
				}
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
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

	@Override
	protected Document getNewItem() {
		Document group = new Document();
		String baseName = "Document ";
		int i = 1;
		group.setName(baseName + i);
		while (getByName(group.getName()) != null) {
			i++;
			group.setName(baseName + i);
		}
		return group;
	}


	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.DOCUMENT;
	}


	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		
	}

}
