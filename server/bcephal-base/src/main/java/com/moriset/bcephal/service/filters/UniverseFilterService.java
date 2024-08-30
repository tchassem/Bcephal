/**
 * 
 */
package com.moriset.bcephal.service.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.repository.filters.UniverseFilterRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class UniverseFilterService extends PersistentService<UniverseFilter, UniverseFilter> {

	@Autowired
	UniverseFilterRepository universeFilterRepository;

	@Autowired
	AttributeFilterService attributeFilterService;

	@Autowired
	SpotFilterService spotFilterService;

	@Autowired
	MeasureFilterService measureFilterService;

	@Autowired
	PeriodFilterService periodFilterService;

	@Override
	public UniverseFilterRepository getRepository() {
		return universeFilterRepository;
	}

	public UniverseFilter save(UniverseFilter filter) {
		if (filter != null) {
			log.trace("Try to save UniverseFilter : {}", filter);
			if (filter.getAttributeFilter() != null) {
				attributeFilterService.save(filter.getAttributeFilter());
			}
			if (filter.getMeasureFilter() != null) {
				measureFilterService.save(filter.getMeasureFilter());
			}
			if (filter.getPeriodFilter() != null) {
				periodFilterService.save(filter.getPeriodFilter());
			}
			if (filter.getSpotFilter() != null) {
				spotFilterService.save(filter.getSpotFilter());
			}
			filter = universeFilterRepository.save(filter);
			log.trace("UniverseFilter saved : {}", filter.getId());
		}
		return filter;
	}

	@Override
	public void delete(UniverseFilter filter) {
		if (filter != null && filter.getId() != null) {
			log.trace("Try to delete UniverseFilter : {}", filter);
			universeFilterRepository.deleteById(filter.getId());
			if (filter.getAttributeFilter() != null) {
				attributeFilterService.delete(filter.getAttributeFilter());
			}
			if (filter.getMeasureFilter() != null) {
				measureFilterService.delete(filter.getMeasureFilter());
			}
			if (filter.getPeriodFilter() != null) {
				periodFilterService.delete(filter.getPeriodFilter());
			}
			if (filter.getSpotFilter() != null) {
				spotFilterService.delete(filter.getSpotFilter());
			}
			log.trace("UniverseFilter deleted : {}", filter.getId());
		}
	}

}
