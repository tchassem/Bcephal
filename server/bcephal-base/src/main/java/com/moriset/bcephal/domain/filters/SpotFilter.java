/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "SpotFilter")
@Table(name = "BCP_SPOT_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class SpotFilter implements IPersistent {

	private static final long serialVersionUID = 4090473631976314788L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_filter_seq")
	@SequenceGenerator(name = "spot_filter_seq", sequenceName = "spot_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "filter")
	private List<SpotFilterItem> items;

	@Transient
	private ListChangeHandler<SpotFilterItem> itemListChangeHandler;

	public SpotFilter() {
		items = new ArrayList<SpotFilterItem>();
		itemListChangeHandler = new ListChangeHandler<SpotFilterItem>();
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<SpotFilterItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}

	@JsonIgnore
	public List<SpotFilterItem> getSortedItems() {
		List<SpotFilterItem> items = getItemListChangeHandler().getItems();
		Collections.sort(items, new Comparator<SpotFilterItem>() {
			public int compare(SpotFilterItem o1, SpotFilterItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		return items;
	}

	public SpotFilterItem getItemAtPosition(int position) {
		for (SpotFilterItem item : itemListChangeHandler.getItems()) {
			if (item.getPosition() == item.getPosition()) {
				return item;
			}
		}
		return null;
	}

	public void addItem(SpotFilterItem item) {
		item.setPosition(itemListChangeHandler.getItems().size());
		itemListChangeHandler.addNew(item);
	}

	public void updateItem(SpotFilterItem item) {
		itemListChangeHandler.addUpdated(item);
	}

	public void deleteOrForgetItem(SpotFilterItem item) {
		if (item.getId() != null) {
			deleteItem(item);
		} else {
			forgetItem(item);
		}
	}

	public void deleteItem(SpotFilterItem item) {
		itemListChangeHandler.addDeleted(item);
		for (SpotFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}

	public void forgetItem(SpotFilterItem item) {
		itemListChangeHandler.forget(item);
		for (SpotFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}

	@JsonIgnore
	public SpotFilter copy() {
		SpotFilter copy = new SpotFilter();
		for(SpotFilterItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		return copy;
	}

}
