/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.FileGroupByItem;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.grid.domain.JoinGridType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 
 *
 */
@Entity(name = "SchedulerPlannerItem")
@Table(name = "BCP_SCHEDULER_PLANNER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerItem extends Persistent {

	private static final long serialVersionUID = 3423139639661927140L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_item_seq")
	@SequenceGenerator(name = "scheduler_planner_item_seq", sequenceName = "scheduler_planner_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "schedulerId")
	private SchedulerPlanner schedulerId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "parentId")
	private SchedulerPlannerItemLoop parent;
	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "itemLoop")
	private SchedulerPlannerItemLoop itemLoop;
	
	private int position;
	
	private boolean active;
	
	private String code;
	
	@Enumerated(EnumType.STRING) 
	private SchedulerPlannerItemType type;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	
	private Long objectId;
	
	private String objectName;
	
	private String repository;
	
	private Integer maxRowCountPerFile;
	
	@JsonIgnore
	private String fileGroupByItemsImpl;
	
	@Transient
	private List<FileGroupByItem> fileGroupByItems;
	
	private String comparator;
	
	private BigDecimal decimalValue;
	
	private int temporisationValue;
	
	private boolean stopIfError;
	
	@Enumerated(EnumType.STRING) 
	private SchedulerPlannerItemTemporisationUnit temporisationUnit;
	
	
	@AttributeOverrides({
	    @AttributeOverride(name="decision", 	column = @Column(name="decision1")),
	    @AttributeOverride(name="alarmId", 		column = @Column(name="alarmId1")),
	    @AttributeOverride(name="gotoCode", 		column = @Column(name="gotoCode1"))
	})
	@Embedded	
	private SchedulerPlannerItemAction action1;
	
	@AttributeOverrides({
	    @AttributeOverride(name="decision", 	column = @Column(name="decision2")),
	    @AttributeOverride(name="alarmId", 		column = @Column(name="alarmId2")),
	    @AttributeOverride(name="gotoCode", 		column = @Column(name="gotoCode2"))
	})
	@Embedded	
	private SchedulerPlannerItemAction action2;
	
	
	public SchedulerPlannerItem(){
		this.action1 = new SchedulerPlannerItemAction();
		this.action2 = new SchedulerPlannerItemAction();
		this.fileGroupByItems = new ArrayList<>();
	}
	
	public int getMaxRowCountPerFile(){
		if(maxRowCountPerFile == null) {
			maxRowCountPerFile = 10000;
		}
		return maxRowCountPerFile;
	}
	
	public String toString() {
		return asString();
	}
	
	@Override
	public SchedulerPlannerItem copy() {
		SchedulerPlannerItem copy = new SchedulerPlannerItem();
		copy.setPosition(getPosition());
		copy.setActive(isActive());
		copy.setCode(getCode());
		copy.setType(getType());
		copy.setGridType(getGridType());
		copy.setObjectId(getObjectId());
		copy.setObjectName(getObjectName());
		copy.setRepository(getRepository());
		copy.setMaxRowCountPerFile(getMaxRowCountPerFile());
		copy.setFileGroupByItemsImpl(getFileGroupByItemsImpl());
		copy.setComparator(getComparator());		
		copy.setDecimalValue(getDecimalValue());		
		copy.setTemporisationValue(getTemporisationValue());		
		copy.setStopIfError(isStopIfError());		
		copy.setTemporisationUnit(getTemporisationUnit());
		copy.setAction1(action1 != null ? action1.copy() : null);
		copy.setAction2(action2 != null ? action2.copy() : null);
		if(itemLoop != null) {
			copy.setItemLoop(itemLoop.copy());
		}		
		return copy;
	}
	
	@PostLoad
	public void initFileGroupByItems() {
		this.fileGroupByItems = new ArrayList<>();	
		if(fileGroupByItemsImpl != null){
			String[] vars = fileGroupByItemsImpl.split(FileGroupByItem.ITEM_SEPARATOR);
			for(String var : vars) {
				FileGroupByItem item = FileGroupByItem.FromText(var);
				if(item != null) {
					this.fileGroupByItems.add(item);
				}
			}
		}
	}
	
	public void initFileGroupByImpl() {		
		ArrayList<String> datas = new ArrayList<>();
		for(FileGroupByItem item : this.fileGroupByItems) {
			datas.add(item.asText());
		}
		this.fileGroupByItemsImpl = String.join(FileGroupByItem.ITEM_SEPARATOR, datas);
	}
	

	public String asString() {		
		if(getType() == SchedulerPlannerItemType.CHECK) {
			return "Check";
		}
		else if(getType() == SchedulerPlannerItemType.ACTION && action1 != null) {
			if(action1.getDecision() == SchedulerPlannerItemDecision.GOTO) {
				return action1.getDecision().name() + " : " + action1.getGotoCode();
			}
			return action1.getDecision().name();
		}
		else if(getType() == SchedulerPlannerItemType.TEMPORISATION) {
			return "Temporisation : " + temporisationValue + " " + (temporisationUnit != null ? temporisationUnit.name() : "");
		}
		else if(getType() == SchedulerPlannerItemType.REFRESH_PUBLICATIONS) {
			return "Refresh publications ";
		}
		else if(getType() == SchedulerPlannerItemType.LOOP && getItemLoop() != null) {
			return getItemLoop().getVariableName();
		}
		return getObjectName();
	}
	
	public void sortItems() {
		if(getType() == SchedulerPlannerItemType.LOOP && getItemLoop() != null) {
			getItemLoop().sortItems();
		}
	}
	
	
}
