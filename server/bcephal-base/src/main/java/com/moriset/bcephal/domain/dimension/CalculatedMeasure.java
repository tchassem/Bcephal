/**
 * 9 févr. 2024 - CalculatedMeasure.java
 *
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.ToString;

/**
 * @author Emmanuel Emmeni
 *
 */
@Entity(name = "CalculatedMeasure")
@Table(name = "BCP_CALCULATED_MEASURE")
@Data
@EqualsAndHashCode(callSuper = false)
public class CalculatedMeasure extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1958343129360522899L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calculated_measure_seq")
	@SequenceGenerator(name = "calculated_measure_seq", sequenceName = "calculated_measure_seq	", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "calculatedMeasure")
	private List<CalculatedMeasureItem> items;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<CalculatedMeasureItem> itemsListChangeHandler;
	
	
	public CalculatedMeasure() {
		this.items = new ArrayList<>();
		this.itemsListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setItems(List<CalculatedMeasureItem> items) {
		this.items = items;
		this.itemsListChangeHandler.setOriginalList(items);
	}
	
	public void sortItems() {
		List<CalculatedMeasureItem> items = this.itemsListChangeHandler.getItems();
		Collections.sort(items, new Comparator<CalculatedMeasureItem>() {
			@Override
			public int compare(CalculatedMeasureItem o1, CalculatedMeasureItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		setItems(items);
		getItems().forEach(item->{item.sortExcludeFilters();});
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		this.items.size();
		this.itemsListChangeHandler.setOriginalList(items);
	}
	
	
	
	@JsonIgnore
	public Set<String> getMeasureNamesForGroupBy() {
		Set<String> cols = new HashSet<>();
		for(CalculatedMeasureItem item : getItems()) {	
			if(item.getType() == DimensionType.MEASURE && item.getMeasureId() != null && !StringUtils.hasText(item.getDimensionFunction())) {
				cols.add(item.getUniverseTableColumnName(dataSourceType));
			}
		}
		return cols;
	}
	
	public boolean hasActiveExcludeFilter() {
		for(CalculatedMeasureItem item : getItems()) {
			if(item.hasActiveExcludeFilter()) {
				return true;
			}
		}
		return false;
	}
	
	@JsonIgnore
	public String toSql() {
		String sql = "";
		for(CalculatedMeasureItem item : getItems()) {			
			String itemSql = item.toSql(dataSourceType);
			if (StringUtils.hasText(itemSql)) {
				String sign = item.getArithmeticOperator();
				boolean isFirst = item.getPosition() == 0;
				sign = StringUtils.hasText(sign) ? " " + sign + " " : " + ";
				sign = isFirst ? "" : sign;
				sql = sql.concat(sign).concat(itemSql);
			}
		}
		return sql;
	}
	

	@Override
	public CalculatedMeasure copy() {		
		CalculatedMeasure copy = new CalculatedMeasure();	
		copy.setName(getName());
		copy.setDataSourceId(getDataSourceId());
		copy.setDataSourceType(getDataSourceType());
		copy.setDataSourceName(getDataSourceName());
		copy.setDescription(getDescription());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setGroup(getGroup());	
		for(CalculatedMeasureItem item : getItemsListChangeHandler().getItems()) {
			copy.getItemsListChangeHandler().addNew(item.copy());
		}		
		return copy;
	}

	


}
