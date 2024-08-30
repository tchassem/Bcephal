/**
 * 
 */
package com.moriset.bcephal.service.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.repository.filters.PeriodFilterItemRepository;
import com.moriset.bcephal.repository.filters.PeriodFilterRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class PeriodFilterService extends PersistentService<PeriodFilter, PeriodFilter> {

	@Autowired
	PeriodFilterRepository filterRepository;

	@Autowired
	PeriodFilterItemRepository itemRepository;

	@Override
	public PeriodFilterRepository getRepository() {
		return filterRepository;
	}

	public PeriodFilter save(PeriodFilter filter) {
		log.trace("Try to save PeriodFilter : {}", filter);
		if (filter != null) {
			ListChangeHandler<PeriodFilterItem> items = filter.getItemListChangeHandler();
			filter = filterRepository.save(filter);
			PeriodFilter id = filter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save PeriodFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("PeriodFilterItem saved :  {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save PeriodFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("PeriodFilterItem saved :  {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete PeriodFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("PeriodFilterItem deleted :  {}", item.getId());
				}
			});
			log.trace("PeriodFilter saved : {}", filter.getId());
		}
		return filter;
	}

	public void delete(PeriodFilter filter) {
		if (filter != null && filter.getId() != null) {
			log.trace("Try to delete PeriodFilter : {}", filter);
			filter.getItemListChangeHandler().getItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete PeriodFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("PeriodFilterItem deleted :  {}", item.getId());
				}
			});
			filter.getItemListChangeHandler().getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete PeriodFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("PeriodFilterItem deleted :  {}", item.getId());
				}
			});
			filterRepository.deleteById(filter.getId());
			log.trace("PeriodFilter deleted : {}", filter.getId());
		}
	}

}
