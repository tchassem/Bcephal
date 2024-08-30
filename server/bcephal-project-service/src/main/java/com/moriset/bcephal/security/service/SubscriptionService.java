package com.moriset.bcephal.security.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.Subscription;
import com.moriset.bcephal.security.domain.SubscriptionBrowserData;
import com.moriset.bcephal.security.domain.SubscriptionCustomBrowserData;
import com.moriset.bcephal.security.repository.SubscriptionBrowserDataRepository;
import com.moriset.bcephal.security.repository.SubscriptionRepository;
import com.moriset.bcephal.service.BaseService;
import com.moriset.bcephal.service.RequestQueryBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class SubscriptionService implements BaseService<Subscription, SubscriptionCustomBrowserData> {

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	SubscriptionBrowserDataRepository subscriptionBrowserDataRepository;

	/**
	 * Saves the given Subscription in the DB.
	 * 
	 * @param subscription Subscription to save
	 * @return saved Subscription
	 */
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public Subscription save(Subscription subscription) {
		log.trace("Try to save subscription : Name = {}", subscription.getName());
		return subscriptionRepository.saveAndFlush(subscription);
	}

	/**
	 * Retrieves project with the given ID.
	 * 
	 * @param id ID of project to find
	 * @return Project with the given ID
	 */
	public Optional<SubscriptionBrowserData> findById(Long id) {
		log.trace("Find subscription by ID: {}", id);
		return subscriptionBrowserDataRepository.findById(id);
	}

	/**
	 * Find all Subscriptions
	 * 
	 * @return
	 */
	public List<SubscriptionBrowserData> findAll() {
		log.trace("Try to retrieve all subscriptions");
		List<SubscriptionBrowserData> subscriptions = subscriptionBrowserDataRepository.findAll();
		return subscriptions;
	}

	/**
	 * Find default subscription
	 * 
	 * @return
	 */
	public List<SubscriptionBrowserData> findDefaultSubscription() {
		log.trace("Try to retrieve default subcription");
		return subscriptionBrowserDataRepository.findByDefaultSubscription(true);
	}

	@Override
	public Specification<Subscription> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Subscription> qBuilder = new RequestQueryBuilder<Subscription>(root, query, cb);
			qBuilder.select(SubscriptionCustomBrowserData.class);
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if(filter.getColumnFilters() != null) {
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	@Override
	public SubscriptionRepository getRepository() {
		return subscriptionRepository;
	}

	@Override
	public SubscriptionCustomBrowserData getNewBrowserData(Subscription item) {
		return new SubscriptionCustomBrowserData(item);
	}

	/**
	 * get Main Object by name
	 */
	public Subscription getByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}

	public Subscription getById(Long id) {
		log.debug("Try to  get by id : {}", id);
		if (getRepository() == null && id != null) {
			return null;
		}
		Optional<Subscription> item = getRepository().findById(id);
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}

	public EditorData<Subscription> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<Subscription> data = new EditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId()));
		}
		return data;
	}

	private Subscription getNewItem() {
		Subscription item = new Subscription();
		int i = 2;
		String baseName = "Client ";
		item.setName(baseName + i);
		while (getByName(item.getName()) != null) {
			i++;
			item.setName(baseName + i);
		}
		return item;
	}
}
