/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.LoaderData;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.filters.UniverseFilter;

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

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutine")
@Table(name = "BCP_TRANSFORMATION_ROUTINE")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutine extends SchedulableObject {
	
	private static final long serialVersionUID = 2662772743371652900L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_seq")
	@SequenceGenerator(name = "transformation_routine_seq", sequenceName = "transformation_routine_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;
	
	private boolean confirmAction;
    	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "filter")
    private UniverseFilter filter;
    
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
    @JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "routine")
	private List<TransformationRoutineItem> items;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<TransformationRoutineItem> itemListChangeHandler;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@Transient 
	private LoaderData loaderData;
    
	
	public TransformationRoutine() {
		this.confirmAction = true;		
		this.itemListChangeHandler = new ListChangeHandler<TransformationRoutineItem>();
		this.dataSourceType = DataSourceType.UNIVERSE;
	}
	
	public void setItems(List<TransformationRoutineItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		items.size();		
		this.itemListChangeHandler.setOriginalList(items);
	}
	
	public void sortItems() {
		this.setItems(this.getItemListChangeHandler().getItems());
		Collections.sort(getItems(), new Comparator<TransformationRoutineItem>() {
			public int compare(TransformationRoutineItem item1, TransformationRoutineItem item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		for(TransformationRoutineItem item : this.getItems()) {
			item.sortItems();
		}
	}
	
	
	@Override
	public TransformationRoutine copy() {
		TransformationRoutine copy = new TransformationRoutine();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setDataSourceId(dataSourceId);
		copy.setDataSourceType(dataSourceType);
		copy.setDataSourceName(dataSourceName);
		copy.setDescription(getDescription());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setActive(isActive());
		copy.setScheduled(isScheduled());
		copy.setCronExpression(getCronExpression());
		copy.setFilter(getFilter() != null ? getFilter().copy() : null);
		for(TransformationRoutineItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}	
		return copy;
	}
	
		
}


