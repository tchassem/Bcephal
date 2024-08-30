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
@jakarta.persistence.Entity(name = "MeasureFilter")
@Table(name = "BCP_MEASURE_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class MeasureFilter implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measure_filter_seq")
	@SequenceGenerator(name = "measure_filter_seq", sequenceName = "measure_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "filter")
	private List<MeasureFilterItem> items;

	@Transient
	private ListChangeHandler<MeasureFilterItem> itemListChangeHandler;

	public MeasureFilter() {
		items = new ArrayList<MeasureFilterItem>();
		itemListChangeHandler = new ListChangeHandler<MeasureFilterItem>();
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<MeasureFilterItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}

	@JsonIgnore
	public List<MeasureFilterItem> getSortedItems() {
		List<MeasureFilterItem> items = getItemListChangeHandler().getItems();
		Collections.sort(items, new Comparator<MeasureFilterItem>() {
			public int compare(MeasureFilterItem o1, MeasureFilterItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		return items;
	}

	public MeasureFilterItem getItemAtPosition(int position) {
		for (MeasureFilterItem item : itemListChangeHandler.getItems()) {
			if (item.getPosition() == item.getPosition()) {
				return item;
			}
		}
		return null;
	}

	public void addItem(MeasureFilterItem item) {
		item.setPosition(itemListChangeHandler.getItems().size());
		itemListChangeHandler.addNew(item);
	}

	public void updateItem(MeasureFilterItem item) {
		itemListChangeHandler.addUpdated(item);
	}

	public void deleteOrForgetItem(MeasureFilterItem item) {
		if (item.getId() != null) {
			deleteItem(item);
		} else {
			forgetItem(item);
		}
	}

	public void deleteItem(MeasureFilterItem item) {
		itemListChangeHandler.addDeleted(item);
		for (MeasureFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}

	public void forgetItem(MeasureFilterItem item) {
		itemListChangeHandler.forget(item);
		for (MeasureFilterItem child : itemListChangeHandler.getItems()) {
			if (child.getPosition() > item.getPosition()) {
				child.setPosition(child.getPosition() - 1);
				itemListChangeHandler.addUpdated(child);
			}
		}
	}
	
	@JsonIgnore
	public String toSql(List<VariableValue> variableValues) {
		String sql = "";
		for(MeasureFilterItem item : getSortedItems()) {			
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
		};
		return sql;
	}
	
	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters, List<VariableValue> variableValues) {
		for(MeasureFilterItem item : getSortedItems()) {	
			boolean response = item.containsExcludedDimensions(excludeFilters, variableValues);
			if(response) {
				return true;
			}
		}
		return false;
	}
	
	public void add(MeasureFilter filter) {
		if(filter != null) {
			int position = getItemListChangeHandler().getItems().size();
			for(MeasureFilterItem item : filter.getSortedItems()) {
				item.setPosition(position++);
				getItemListChangeHandler().addNew(item);
			}
		}
	}
	
	

	@JsonIgnore
	public MeasureFilter copy() {
		MeasureFilter copy = new MeasureFilter();
		for(MeasureFilterItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		return copy;
	}

}
