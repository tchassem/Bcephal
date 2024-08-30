package com.moriset.bcephal.service.filters;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.SpotFilter;
import com.moriset.bcephal.domain.filters.SpotFilterItem;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.repository.filters.SpotFilterItemRepository;
import com.moriset.bcephal.repository.filters.SpotFilterRepository;
import com.moriset.bcephal.service.InitiationService;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.servlet.http.HttpSession;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class SpotFilterService extends PersistentService<SpotFilter, BrowserData> {

	@Autowired
	SpotFilterRepository spotFilterRepository;

	@Autowired
	InitiationService initiationService;

	@Autowired
	SpotFilterItemRepository spotFilterItemRepository;

	@Override
	public PersistentRepository<SpotFilter> getRepository() {
		return spotFilterRepository;
	}

	public EditorData<SpotFilter> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		EditorData<SpotFilter> data = new EditorData<SpotFilter>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		data.setModels(initiationService.getModels(session, locale));
		data.setPeriods(initiationService.getPeriods(session, locale));
		data.setMeasures(initiationService.getMeasures(session, locale));
		return data;
	}

	protected SpotFilter getNewItem() {
		SpotFilter spotFilter = new SpotFilter();

		return spotFilter;
	}

	protected BrowserData getNewBrowserData(SpotFilter item) {
		return new BrowserData(item.getId(), null);
	}

	protected Specification<SpotFilter> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<SpotFilter> qBuilder = new RequestQueryBuilder<SpotFilter>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("group"),
					root.get("visibleInShortcut"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	public SpotFilter save(SpotFilter spotFilter) {
		log.debug("Try to  Save spotFilter : {}", spotFilter);
		try {
			if (spotFilter == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.spot", new Object[] { spotFilter },
						Locale.ENGLISH);
				throw new BcephalException(message);
			}
			ListChangeHandler<SpotFilterItem> items = spotFilter.getItemListChangeHandler();

			spotFilter = spotFilterRepository.save(spotFilter);
			SpotFilter id = spotFilter;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save spotFilterItem : {}", item);
				item.setFilter(id);
				spotFilterItemRepository.save(item);
				log.trace("GrilleColumn saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save spotFilterItem : {}", item);
				item.setFilter(id);
				spotFilterItemRepository.save(item);
				log.trace("spotFilterItem saved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete spotFilterItem : {}", item);
					spotFilterItemRepository.deleteById(item.getId());
					log.trace("GrilleColumn deleted : {}", item.getId());
				}
			});
			log.debug("spot successfully to save : {} ", spotFilter);

			return spotFilter;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save spot : {}", spotFilter, e);
			String message = getMessageSource().getMessage("unable.to.save.spot", new Object[] { spotFilter },
					Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public void delete(SpotFilter spotFilter) {
		ListChangeHandler<SpotFilterItem> items = spotFilter.getItemListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete spotFilterItem : {}", item);
				spotFilterItemRepository.deleteById(item.getId());
				log.trace("spotFilterItem deleted : {}", item.getId());
			}
		});

		spotFilterRepository.deleteById(spotFilter.getId());

		log.debug("spotFilter successfully to delete : {} ", spotFilter);
		return;
	}

}
