package com.moriset.bcephal.dashboard.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.ListChangeHandler;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "UniverseDynamicFilter")
@Table(name = "BCP_UNIVERSE_DYNAMIC_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class UniverseDynamicFilter implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "universe_dynanic_filter_seq")
	@SequenceGenerator(name = "universe_dynanic_filter_seq", sequenceName = "universe_dynanic_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private boolean usingDynamicFilter;
	
	private String cronExpressionDynamicFilter;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "filter")
	private List<UniverseDynamicFilterItem> items;

	@Transient
	private ListChangeHandler<UniverseDynamicFilterItem> itemsListChangeHandler;
	
	public UniverseDynamicFilter() {
		items = new ArrayList<>();
		itemsListChangeHandler = new ListChangeHandler<>();
	}
	
	public String getCronExpressionDynamicFilter() {
		if(!StringUtils.hasText(cronExpressionDynamicFilter)) {
			cronExpressionDynamicFilter = "10";
		}
		return cronExpressionDynamicFilter;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<UniverseDynamicFilterItem> items) {
		this.items = items;
		itemsListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemsListChangeHandler.setOriginalList(items);
	}

	@JsonIgnore
	public List<UniverseDynamicFilterItem> getSortedItems() {
		List<UniverseDynamicFilterItem> items = getItemsListChangeHandler().getItems();
		Collections.sort(items, new Comparator<UniverseDynamicFilterItem>() {
			public int compare(UniverseDynamicFilterItem o1, UniverseDynamicFilterItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		return items;
	}

	public UniverseDynamicFilterItem getItemAtPosition(int position) {
		for (UniverseDynamicFilterItem item : itemsListChangeHandler.getItems()) {
			if (item.getPosition() == item.getPosition()) {
				return item;
			}
		}
		return null;
	}

	public void addItem(UniverseDynamicFilterItem item) {
		item.setPosition(itemsListChangeHandler.getItems().size());
		itemsListChangeHandler.addNew(item);
	}

	public void updateItem(UniverseDynamicFilterItem item) {
		itemsListChangeHandler.addUpdated(item);
	}

	public void deleteOrForgetItem(UniverseDynamicFilterItem item) {
		if (item.getId() != null) {
			deleteItem(item);
		} else {
			forgetItem(item);
		}
	}

	public void deleteItem(UniverseDynamicFilterItem item) {
		itemsListChangeHandler.addDeleted(item);
		for (UniverseDynamicFilterItem child : itemsListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemsListChangeHandler.addUpdated(child);
			}
		}
	}

	public void forgetItem(UniverseDynamicFilterItem item) {
		itemsListChangeHandler.forget(item);
		for (UniverseDynamicFilterItem child : itemsListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemsListChangeHandler.addUpdated(child);
			}
		}
	}
	
	public UniverseDynamicFilter copy() {
		UniverseDynamicFilter copy = new UniverseDynamicFilter();
		copy.setUsingDynamicFilter(usingDynamicFilter);
		copy.setCronExpressionDynamicFilter(cronExpressionDynamicFilter);
		for (UniverseDynamicFilterItem field : this.getItemsListChangeHandler().getItems()) {
			if (field == null) continue;
			UniverseDynamicFilterItem copyField = field.copy();
			copy.getItemsListChangeHandler().addNew(copyField);
		}
		return copy;
	}
	
}
