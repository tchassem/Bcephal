/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.SchedulableObject;

import jakarta.persistence.Entity;
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
 * @author
 *
 */
@Entity(name = "SchedulerPlanner")
@Table(name = "BCP_SCHEDULER_PLANNER")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlanner extends SchedulableObject {
	
	private static final long serialVersionUID = -8884799559291460702L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_seq")
	@SequenceGenerator(name = "scheduler_planner_seq", sequenceName = "scheduler_planner_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "schedulerId")
	private List<SchedulerPlannerItem> items;

	private boolean confirmAction;
	
	@Transient
	private ListChangeHandler<SchedulerPlannerItem> itemListChangeHandler;
	
	public SchedulerPlanner(){
		this.confirmAction = true;
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
	
	@Override
	public SchedulerPlanner copy() {
		SchedulerPlanner copy = new SchedulerPlanner();
		copy.setName(getName());
		copy.setDescription(getDescription());
		copy.setGroup(getGroup());
		copy.setActive(isActive());
		copy.setScheduled(isScheduled());
		copy.setCronExpression(getCronExpression());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		
		for(SchedulerPlannerItem item : getItemListChangeHandler().getItems()) {
			SchedulerPlannerItem copyItem = item.copy();
			copy.getItemListChangeHandler().addNew(copyItem);
		}
		
		return copy;
	}
	
	public SchedulerPlannerItem getItemByCode(String code) {
		for(SchedulerPlannerItem item : getItemListChangeHandler().getItems()) {
			if(item.getCode() != null && item.getCode().equals(code)) {
				return item;
			}
		}
		return null;
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
	
	public Path buildLogFilePath(String projectDataDir, String projectCode) {		
		String name = "schedulerlog-" + getId() + ".log";		
		Path path = Paths.get(projectDataDir, "scheduler", "logs", projectCode, name);			
		return path;
	}

}
