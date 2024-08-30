/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.VariableInterval;
import com.moriset.bcephal.grid.domain.VariableReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity(name = "SchedulerPlannerItemLoop")
@Table(name = "BCP_SCHEDULER_PLANNER_ITEM_LOOP")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerItemLoop extends Persistent {

	private static final long serialVersionUID = -3082756965550842208L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_item_loop_seq")
	@SequenceGenerator(name = "scheduler_planner_item_loop_seq", sequenceName = "scheduler_planner_item_loop_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "schedulerItemId")
	private SchedulerPlannerItem schedulerItem;
	
	private String variableName;
	
	@Enumerated(EnumType.STRING)
	private DimensionType variableType;
		
	@ManyToOne @jakarta.persistence.JoinColumn(name = "variableReference")
	private VariableReference variableReference;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "variableInterval")
	private VariableInterval variableInterval;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<SchedulerPlannerItem> items;
	
	@Transient
	private ListChangeHandler<SchedulerPlannerItem> itemListChangeHandler;
	
	
	public SchedulerPlannerItemLoop(){
		this.itemListChangeHandler = new ListChangeHandler<SchedulerPlannerItem>();
	}
	
	public void setItems(List<SchedulerPlannerItem> items) {
		this.items = items;
		this.itemListChangeHandler.setOriginalList(items);
	}
	
	@PostLoad
	public void initListChangeHandler() {		
		items.forEach(x->{});
		this.itemListChangeHandler.setOriginalList(items);	
	}
	
	public void sortItems() {		
		List<SchedulerPlannerItem> items = getItemListChangeHandler().getItems();	
		Collections.sort(items, new Comparator<SchedulerPlannerItem>() {
			@Override
			public int compare(SchedulerPlannerItem value1, SchedulerPlannerItem value2) {
				return value1.getPosition() - value2.getPosition();
			}
		});
		setItems(items);
		for(SchedulerPlannerItem item : items) {
			item.sortItems();
		}
	}
	
	
	@Override
	public SchedulerPlannerItemLoop copy() {
		SchedulerPlannerItemLoop copy = new SchedulerPlannerItemLoop();
		copy.setVariableType(getVariableType());
		copy.setVariableName(getVariableName());		
		if(variableReference != null) {
			copy.setVariableReference(variableReference.copy());
		}
		if(variableInterval != null) {
			copy.setVariableInterval(variableInterval.copy());
		}
		for(SchedulerPlannerItem item : getItemListChangeHandler().getItems()) {
			SchedulerPlannerItem copyItem = item.copy();
			copy.getItemListChangeHandler().addNew(copyItem);
		}
		return copy;
	}
		
	
}
