/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;

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
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineMapping")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_MAPPING")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineMapping extends Persistent {

	private static final long serialVersionUID = 8409292182754398068L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_mapping_seq")
	@SequenceGenerator(name = "transformation_routine_mapping_seq", sequenceName = "transformation_routine_mapping_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long gridId;
	
	private JoinGridType gridType;
	
	private Long valueColumnId;
	
	@JsonIgnore
	private String mappingColumnsIpml;
	
	@Transient
	private List<Long> mappingColumnIds;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "mappingId")
	private List<TransformationRoutineMappingCondition> conditions;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient 
	private ListChangeHandler<TransformationRoutineMappingCondition> conditionListChangeHandler;
	
	
	@Transient @JsonIgnore 
	private AbstractSmartGrid<?> grid;
	
	
	
	
	public TransformationRoutineMapping(){
		this.conditionListChangeHandler = new ListChangeHandler<>();
		mappingColumnIds = new ArrayList<Long>();
	}
	
	public void setConditions(List<TransformationRoutineMappingCondition> conditions) {
		this.conditions = conditions;
		conditionListChangeHandler.setOriginalList(conditions);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		conditions.size();		
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	public void sortConditions() {
		this.setConditions(this.getConditionListChangeHandler().getItems());
		Collections.sort(getConditions(), new Comparator<TransformationRoutineMappingCondition>() {
			public int compare(TransformationRoutineMappingCondition item1, TransformationRoutineMappingCondition item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
	}
	
//	@PostLoad
//	public void buildMappingColumns() {
//		this.mappingColumnIds = new ArrayList<>();
//		if(StringUtils.hasText(this.mappingColumnsIpml)) {
//			String separator = ";";
//			for(String id : this.mappingColumnsIpml.split(separator)) {
//				if(StringUtils.hasText(id)) {
//					this.mappingColumnIds.add(Long.valueOf(id));
//				}
//			}
//		}
//	}
//	
//	@PrePersist @PreUpdate
//	public void buildMappingColumnsRepresentation() {
//		this.mappingColumnsIpml = null;
//		if(this.mappingColumnIds != null) {
//			String separator = ";";
//			for(Long id : this.mappingColumnIds) {
//				if(!StringUtils.hasText(this.mappingColumnsIpml)) {
//					this.mappingColumnsIpml = "" + id;					
//				}
//				else {
//					this.mappingColumnsIpml += separator + id;
//				}
//			}
//		}
//	}
	
	
	
	
	@Override
	public TransformationRoutineMapping copy() {
		TransformationRoutineMapping copy = new TransformationRoutineMapping();
		copy.setGridId(getGridId());
		copy.setValueColumnId(getValueColumnId());
		copy.setMappingColumnsIpml(getMappingColumnsIpml());
		copy.setMappingColumnIds(getMappingColumnIds());
		copy.setGrid(getGrid());
		copy.setGridType(getGridType());
		for(TransformationRoutineMappingCondition condition : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(condition.copy());
		}
		return copy;
	}
	
}
