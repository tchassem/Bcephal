/**
 * 
 */
package com.moriset.bcephal.service.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.repository.filters.MeasureFilterItemRepository;
import com.moriset.bcephal.repository.filters.MeasureFilterRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class MeasureFilterService extends PersistentService<MeasureFilter, MeasureFilter> {

	@Autowired
	MeasureFilterRepository filterRepository;

	@Autowired
	MeasureFilterItemRepository itemRepository;

	@Override
	public MeasureFilterRepository getRepository() {
		return filterRepository;
	}

	public MeasureFilter save(MeasureFilter filter) {
		if (filter != null) {
			log.trace("Try to save MeasureFilter : {}", filter);
			ListChangeHandler<MeasureFilterItem> items = filter.getItemListChangeHandler();
			filter = filterRepository.save(filter);
			MeasureFilter id = filter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save MeasureFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("MeasureFilterItem saved :  {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save MeasureFilterItem : {}", item);
				item.setFilter(id);
				itemRepository.save(item);
				log.trace("MeasureFilterItem saved :  {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete MeasureFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("MeasureFilterItem deleted :  {}", item.getId());
				}
			});
			log.trace("MeasureFilter saved : {}", filter.getId());
		}
		return filter;
	}

	@Override
	public void delete(MeasureFilter filter) {
		if (filter != null && filter.getId() != null) {
			log.trace("Try to delete MeasureFilter : {}", filter);
			filter.getItemListChangeHandler().getItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete MeasureFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("MeasureFilterItem deleted :  {}", item.getId());
				}
			});
			filter.getItemListChangeHandler().getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete MeasureFilterItem : {}", item.getId());
					itemRepository.deleteById(item.getId());
					log.trace("MeasureFilterItem deleted :  {}", item.getId());
				}
			});
			filterRepository.deleteById(filter.getId());
			log.trace("MeasureFilter deleted : {}", filter.getId());
		}
	}

}
