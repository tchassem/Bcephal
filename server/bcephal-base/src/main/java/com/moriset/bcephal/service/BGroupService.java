/**
 * 
 */
package com.moriset.bcephal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.GroupBrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.repository.BGroupRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Service
@Slf4j
public class BGroupService extends MainObjectService<BGroup, BrowserData> {
	
	@Autowired
	BGroupRepository attributeRepository;

	@Override
	public BGroupRepository getRepository() {
		return attributeRepository;
	}
	
	@Override
	public BGroup getDefaultGroup() {
		try {
			BGroup group = getByName(BGroup.DEFAULT_GROUP_NAME);
			return group;
		} catch (NoResultException e) {
			return null;
		}catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while retrieving default group", e);
		}
		return null;
	}
	
	@Override
	public EditorData<BGroup> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<BGroup> data = new EditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		if(filter.getDataSourceType() == null 
				|| filter.getDataSourceType().isUniverse() 
				|| filter.getDataSourceType().isInputGrid()
				|| filter.getDataSourceType().isReportGrid()) {
			initEditorData(data, session, locale);
		}	
		return data;
	}

	@Override
	public BGroup save(BGroup group, Locale locale) {
		log.debug("Try to  Save group : {}", group);
		try {
			if (group == null) {
				String message = messageSource.getMessage("unable.to.save.null.group", new Object[] { group }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			if (!StringUtils.hasLength(group.getName())) {
				String message = messageSource.getMessage("unable.to.save.group.with.empty.name",
						new String[] { group.getName() }, locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
			List<BGroup> newItems = group.getChildrenListChangeHandler().getNewItems();
			List<BGroup> updatedItems = group.getChildrenListChangeHandler().getUpdatedItems();
			List<BGroup> deletedItems = group.getChildrenListChangeHandler().getDeletedItems();
			if(group.getCreationDate() == null) {
				group.setCreationDate(new Timestamp(System.currentTimeMillis()));
				group.setModificationDate(group.getCreationDate());
			}else {
					group.setModificationDate(new Timestamp(System.currentTimeMillis()));				
				}
			group = super.save(group, locale);
			final BGroup attribute_ = group;
			if (newItems != null) {
				newItems.forEach(item -> {
					item.setGroup(attribute_);
					save(item, locale);
				});
			}
			if (updatedItems != null) {
				updatedItems.forEach(item -> {
					item.setGroup(attribute_);
					save(item, locale);
				});
			}
			if (deletedItems != null) {
				deletedItems.forEach(item -> {
					delete(item);
				});
			}
			log.debug("group successfully to save : {} ", group);
			return getById(group.getId());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save group : {}", group, e);
			String message = messageSource.getMessage("unable.to.save.attribute", new Object[] { group }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void delete(BGroup group) {
		log.debug("Try to  delete group : {}", group);
		if (group == null || group.getId() == null) {
			return;
		}
		if (group.getChildren() != null) {
			group.getChildren().forEach(item -> {
				delete(item);
			});
		}
		deleteById(group.getId());
		log.debug("group successfully to delete : {} ", group);
	}

	@Override
	protected GroupBrowserData getNewBrowserData(BGroup item) {
		return new GroupBrowserData(item);
	}

	@Override
	protected Specification<BGroup> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {

		return (root, query, cb) -> {
			RequestQueryBuilder<BGroup> qBuilder = new RequestQueryBuilder<BGroup>(root, query, cb);
			qBuilder.select(GroupBrowserData.class, root.get("id"), root.get("name"), root.get("visibleInShortcut"),
					root.get("creationDate"), root.get("modificationDate"));
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
	protected BGroup getNewItem() {
		BGroup group = new BGroup();
		String baseName = "Group ";
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel,Long profileId) {
		// TODO Auto-generated method stub
		
	}

	

}
