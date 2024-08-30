/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;

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
@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "AttributeFilter")
@Table(name = "BCP_ATTRIBUTE_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class AttributeFilter implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_filter_seq")
	@SequenceGenerator(name = "attribute_filter_seq", sequenceName = "attribute_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "filter")
	private List<AttributeFilterItem> items;

	@Transient
	private ListChangeHandler<AttributeFilterItem> itemListChangeHandler;

	public AttributeFilter() {
		items = new ArrayList<AttributeFilterItem>();
		itemListChangeHandler = new ListChangeHandler<AttributeFilterItem>();
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<AttributeFilterItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}

	@JsonIgnore
	public List<AttributeFilterItem> getSortedItems() {
		List<AttributeFilterItem> items = getItemListChangeHandler().getItems();
		Collections.sort(items, new Comparator<AttributeFilterItem>() {
			public int compare(AttributeFilterItem o1, AttributeFilterItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		return items;
	}

	public AttributeFilterItem getItemAtPosition(int position) {
		for (AttributeFilterItem item : itemListChangeHandler.getItems()) {
			if (item.getPosition() == item.getPosition()) {
				return item;
			}
		}
		return null;
	}

	public void addItem(AttributeFilterItem item) {
		item.setPosition(itemListChangeHandler.getItems().size());
		itemListChangeHandler.addNew(item);
	}

	public void updateItem(AttributeFilterItem item) {
		itemListChangeHandler.addUpdated(item);
	}

	public void deleteOrForgetItem(AttributeFilterItem item) {
		if (item.getId() != null) {
			deleteItem(item);
		} else {
			forgetItem(item);
		}
	}

	public void deleteItem(AttributeFilterItem item) {
		itemListChangeHandler.addDeleted(item);
		for (AttributeFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}

	public void forgetItem(AttributeFilterItem item) {
		itemListChangeHandler.forget(item);
		for (AttributeFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}
	
	@JsonIgnore
	public String toSql(List<VariableValue> variableValues) {
		String sql = "";
		for(AttributeFilterItem item : getSortedItems()) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				boolean isFirst = item.getPosition() == 0;
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);
			}
		}
		return sql;
	}

	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters, List<VariableValue> variableValues) {
		for(AttributeFilterItem item : getSortedItems()) {	
			boolean response = item.containsExcludedDimensions(excludeFilters, variableValues);
			if(response) {
				return true;
			}
		}
		return false;
	}
	
	public void add(AttributeFilter filter) {
		if(filter != null) {
			int position = getItemListChangeHandler().getItems().size();
			for(AttributeFilterItem item : filter.getSortedItems()) {
				item.setPosition(position++);
				getItemListChangeHandler().addNew(item);
			}
		}
	}

	@JsonIgnore
	public AttributeFilter copy() {
		AttributeFilter copy = new AttributeFilter();
		for(AttributeFilterItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		return copy;
	}


}
