package com.moriset.bcephal.dashboard.domain;

import java.util.ArrayList;
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

@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "DynamicPeriodFilter")
@Table(name = "BCP_DYNAMIC_PERIOD_FILTER")
@Data
@EqualsAndHashCode(callSuper = false)
public class DynamicPeriodFilter implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dynamic_period_filter_seq")
	@SequenceGenerator(name = "dynamic_period_filter_seq", sequenceName = "dynamic_period_filter_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "filter")
	private List<DynamicPeriodFilterItem> items;

	@Transient
	private ListChangeHandler<DynamicPeriodFilterItem> itemListChangeHandler;

	public DynamicPeriodFilter() {
		items = new ArrayList<DynamicPeriodFilterItem>();
		itemListChangeHandler = new ListChangeHandler<DynamicPeriodFilterItem>();
	}
	
	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}
}
