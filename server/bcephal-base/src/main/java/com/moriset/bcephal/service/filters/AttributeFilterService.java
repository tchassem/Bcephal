/**
 * 
 */
package com.moriset.bcephal.service.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.repository.filters.AttributeFilterItemRepository;
import com.moriset.bcephal.repository.filters.AttributeFilterRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class AttributeFilterService extends PersistentService<AttributeFilter, AttributeFilter> {

	@Autowired
	AttributeFilterRepository filterRepository;

	@Autowired
	AttributeFilterItemRepository itemRepository;

	@Override
	public AttributeFilterRepository getRepository() {
		return filterRepository;
	}

	public AttributeFilter save(AttributeFilter filter) {
		if (filter != null) {
			log.trace("Try to save AttributeFilter : {}", filter);
			ListChangeHandler<AttributeFilterItem> items = filter.getItemListChangeHandler();
			filter = filterRepository.save(filter);
			AttributeFilter id = filter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save AttributeFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("AttributeFilterItem saved :  {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save AttributeFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("AttributeFilterItem saved :  {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete AttributeFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("AttributeFilterItem deleted :  {}", item.getId());
				}
			});
			log.trace("AttributeFilter saved : {}", filter.getId());
		}
		return filter;
	}

	public void delete(AttributeFilter filter) {
		if (filter != null && filter.getId() != null) {
			log.trace("Try to delete AttributeFilter : {}", filter);
			filter.getItemListChangeHandler().getItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete AttributeFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("AttributeFilterItem deleted :  {}", item.getId());
				}
			});
			filter.getItemListChangeHandler().getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete AttributeFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("AttributeFilterItem deleted :  {}", item.getId());
				}
			});
			filterRepository.deleteById(filter.getId());
			log.trace("AttributeFilter deleted : {}", filter.getId());
		}
	}

}
