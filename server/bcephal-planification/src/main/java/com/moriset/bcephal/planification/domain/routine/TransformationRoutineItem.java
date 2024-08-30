/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.TransformationRoutineRanking;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.AttributeOperator;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;

import jakarta.persistence.Embedded;
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
@Entity(name = "TransformationRoutineItem")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineItem extends Persistent {

	private static final long serialVersionUID = -6329480263354521425L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_item_seq")
	@SequenceGenerator(name = "transformation_routine_item_seq", sequenceName = "transformation_routine_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	private String name;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "routine")
	private TransformationRoutine routine;
	
	private String description;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	private Long targetGridId;
	
	private Long targetDimensionId;	
	
	private boolean applyOnlyIfEmpty;
	
	private boolean active;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "spot")
	private TransformationRoutineSpot spot;
	
	@Embedded
	private TransformationRoutineRanking ranking;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "sourceField")
	private TransformationRoutineField sourceField;
	
	@ManyToOne @JoinColumn(name = "filter")
	private UniverseFilter filter;
	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "routineItem")
	private List<TransformationRoutineCalculateItem> calculateItems;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<TransformationRoutineCalculateItem> calculateItemListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "routineItem")
	private List<TransformationRoutineConcatenateItem> concatenateItems;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient 
	private ListChangeHandler<TransformationRoutineConcatenateItem> concatenateItemListChangeHandler;

	@Transient @JsonIgnore 
	private AbstractSmartGrid<?> grid;
	
	@Transient @JsonIgnore 
	private DataSourceType dataSourceType;
	
	@Transient @JsonIgnore 
	private Long dataSourceId;
	
	public TransformationRoutineItem() {
		this.calculateItemListChangeHandler = new ListChangeHandler<TransformationRoutineCalculateItem>();
		this.concatenateItemListChangeHandler = new ListChangeHandler<TransformationRoutineConcatenateItem>();
		this.ranking = new TransformationRoutineRanking();
	}
	
	public TransformationRoutineRanking getRanking(){
		if(ranking == null) {
			ranking = new TransformationRoutineRanking();
		}
		return ranking;
	}
	
	public void setCalculateItems(List<TransformationRoutineCalculateItem> calculateItems) {
		this.calculateItems = calculateItems;
		calculateItemListChangeHandler.setOriginalList(calculateItems);
	}
	
	public void setConcatenateItems(List<TransformationRoutineConcatenateItem> concatenateItems) {
		this.concatenateItems = concatenateItems;
		concatenateItemListChangeHandler.setOriginalList(concatenateItems);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		calculateItems.size();		
		this.calculateItemListChangeHandler.setOriginalList(calculateItems);		
		concatenateItems.size();		
		this.concatenateItemListChangeHandler.setOriginalList(concatenateItems);
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return type != null && type.isMeasure();
	}
	
	@JsonIgnore
	public boolean isAttribute() {
		return type != null && type.isAttribute();
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return type != null && type.isPeriod();
	}
	
	@JsonIgnore
	public boolean isDeleteEntry() {
		return sourceField != null && sourceField.getSourceType() == TransformationRoutineSourceType.DELETE_ENTRY;
	}
	
	@JsonIgnore
	public String getUniverseTableTargetColumnName() {
		if (isMeasure()) {
			return new Measure(targetDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName();
		}
		if (isAttribute()) {
			return new Attribute(targetDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName();
		}
		if (isPeriod()) {	
			return new Period(targetDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName();
		}
		return null;
	}
	
	@JsonIgnore
	public Object getFreeValue() {
		return sourceField != null ? sourceField.getFreeValue(type) : null;
	}
	
	public void sortItems() {
		this.setConcatenateItems(getConcatenateItemListChangeHandler().getItems());
		this.setCalculateItems(getCalculateItemListChangeHandler().getItems());
		
		Collections.sort(getConcatenateItems(), new Comparator<TransformationRoutineConcatenateItem>() {
			public int compare(TransformationRoutineConcatenateItem item1, TransformationRoutineConcatenateItem item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		Collections.sort(getCalculateItems(), new Comparator<TransformationRoutineCalculateItem>() {
			public int compare(TransformationRoutineCalculateItem item1, TransformationRoutineCalculateItem item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
	}
	

	@Override
	public TransformationRoutineItem copy() {
		TransformationRoutineItem copy = new TransformationRoutineItem();
		copy.setName(this.getName());
		copy.setType(getType());
		copy.setRoutine(getRoutine());
		copy.setDescription(getDescription());
		copy.setPosition(getPosition());
		copy.setTargetGridId(getTargetGridId());
		copy.setTargetDimensionId(getTargetDimensionId());
		copy.setApplyOnlyIfEmpty(isApplyOnlyIfEmpty());
		copy.setActive(isActive());
		copy.setSourceField(getSourceField() != null ? getSourceField().copy() : null);
		copy.setFilter(getFilter() != null ? getFilter().copy() : null);	
		copy.setSpot(getSpot() != null ? getSpot().copy() : null);
		for(TransformationRoutineCalculateItem item : getCalculateItemListChangeHandler().getItems()) {
			copy.getCalculateItemListChangeHandler().addNew(item.copy());
		}
		for(TransformationRoutineConcatenateItem item : getConcatenateItemListChangeHandler().getItems()) {
			copy.getConcatenateItemListChangeHandler().addNew(item.copy());
		}		
		return copy;
	}
	
	
	public String buildSetReplacePart(String col, Map<String, Object> parameters) {
		String sql = null;	
		String fieldCol = getSourceField().getReplaceUniverseColumnName();	
		if(!StringUtils.hasText(fieldCol)) {
			fieldCol = col;
		}
		if(StringUtils.hasText(fieldCol)) {	
			String paramValue = "value" + System.currentTimeMillis();
			String paramCriteria = "criteria" + System.currentTimeMillis();	
			String criteria = ":" + paramCriteria;
			String param = ":" + paramValue;
			if(getType().isAttribute()) {
				AttributeOperator operation = AttributeOperator.valueOf(getSourceField().getFindOperator());	
				operation = operation != null ? operation : AttributeOperator.EQUALS;
				String condition = "";
				
				if(getSourceField().isFindIgnoreCase()) {		
					String value = getSourceField().isReplaceFoundCharactersOnly() ? " REGEXP_REPLACE ("+ fieldCol + "," + criteria + ", " + param +", 'gi')" 
							: param;
					
					if(operation.isNull()) condition = "(" + fieldCol + " IS NULL OR " + fieldCol + " = '')";
					else if(operation.isNotNull()) condition = "(" + fieldCol + " IS NOT NULL AND " + fieldCol + " <> '')";			    	
					else if(operation.isEquals()) condition = "UPPER("+ fieldCol + ") = UPPER(" + criteria + ")";
					else if(operation.isNotEquals()) condition =  "UPPER("+ fieldCol + ") != UPPER(" + criteria + ")";			    	
					else if(operation.isStartsWith()) condition =  "starts_with(UPPER(" + fieldCol + "), UPPER(" + criteria + "))";
					else if(operation.isEndsWith()) condition =  "ends_with(UPPER(" + fieldCol + "), UPPER(" + criteria + "))";			        
					else if(operation.isContains()) condition =  "REGEXP_MATCH(" + fieldCol + "," + criteria + ",'i') IS NOT NULL";
					else if(operation.isNotContains()) condition =  "REGEXP_MATCH(" + fieldCol + "," + criteria + ",'i') IS NULL";
			    	
					sql = " SET " + col + " = CASE WHEN " + condition + " THEN " + value + " ELSE " + col + " END";
				}
				else {
					String value = getSourceField().isReplaceFoundCharactersOnly() ? " REGEXP_REPLACE ("+ fieldCol + "," + criteria + ", " + param +", 'g')" 
							: param;
										
					if(operation.isNull()) condition = "(" + fieldCol + " IS NULL OR " + fieldCol + " = '')";
					else if(operation.isNotNull()) condition = "(" + fieldCol + " IS NOT NULL AND " + fieldCol + " <> '')";			    	
					else if(operation.isEquals()) condition = fieldCol + " = " + criteria;
					else if(operation.isNotEquals()) condition =  fieldCol + " != " + criteria;			    	
					else if(operation.isStartsWith()) condition =  "starts_with(" + fieldCol + ", " + criteria + ")";
					else if(operation.isEndsWith()) condition =  "ends_with(" + fieldCol + ", " + criteria + ")";			        
					else if(operation.isContains()) condition =  "REGEXP_MATCH(" + fieldCol + "," + criteria + ") IS NOT NULL";
					else if(operation.isNotContains()) condition =  "REGEXP_MATCH(" + fieldCol + "," + criteria + ") IS NULL";
					
					sql = " SET " + col + " = CASE WHEN " + condition + " THEN " + value + " ELSE " + col + " END";
				}				
				parameters.put(paramValue, getSourceField().getReplaceStringValue() != null ? getSourceField().getReplaceStringValue() : "");
				parameters.put(paramCriteria, getSourceField().getStringValue());
			}	
			
		}
		return sql;
	}

	public String buildSetPositionPart(String col, Map<String, Object> parameters) {
		String sql = null;		
		String fieldCol = getSourceField().getPositionUniverseColumnName();			
		if(StringUtils.hasText(fieldCol) && getSourceField().getPositionSourceType() != null) {	
			if(getSourceField().getPositionSourceType().isInterval()) {
				if((getSourceField().getPositionStart() != null && getSourceField().getPositionStart() > 0)
						|| (getSourceField().getPositionEnd() != null && getSourceField().getPositionEnd() > 0) ) {
					
					int start = getSourceField().getPositionStart() != null && getSourceField().getPositionStart() > 0 
							? getSourceField().getPositionStart() : 1;
					if(getSourceField().getPositionEnd() != null && getSourceField().getPositionEnd() > 0) {
						int length = getSourceField().getPositionEnd() - start + 1;
						sql = "substring(" + fieldCol + ", " + start + ", " + length + ")";
					}
					else {
						sql = "substring(" + fieldCol + ", " + start + ")";
					}
				}
				else {
					sql = fieldCol;
				}
			}
			else if(getSourceField().getPositionSourceType().isLastNChar()) {
				if(getSourceField().getPositionStart() != null) {					
					int start = getSourceField().getPositionStart();
					sql = "right(" + fieldCol + ", " + start + ")";					
				}
				else {
					sql = fieldCol;
				}
			}
			else if(getSourceField().getPositionSourceType().isFirstNChar()) {
				if(getSourceField().getPositionStart() != null) {					
					int start = getSourceField().getPositionStart();
					sql = "left(" + fieldCol + ", " + start + ")";					
				}
				else {
					sql = fieldCol;
				}
			}
			else if(getSourceField().getPositionSourceType().isIntervalR()) {
				if((getSourceField().getPositionStart() != null && getSourceField().getPositionStart() > 0)
						|| (getSourceField().getPositionEnd() != null && getSourceField().getPositionEnd() > 0) ) {
					
					int start = getSourceField().getPositionStart() != null && getSourceField().getPositionStart() > 0 
							? getSourceField().getPositionStart() : 1;
					if(getSourceField().getPositionEnd() != null && getSourceField().getPositionEnd() > 0) {
						int length = getSourceField().getPositionEnd() - start + 1;
						sql = "reverse(substring(reverse(" + fieldCol + "), " + start + ", " + length + "))";
					}
					else {
						sql = "reverse(substring(reverse(" + fieldCol + "), " + start + "))";
					}
				}
				else {
					sql = fieldCol;
				}
			}
			else {
				if(StringUtils.hasText(getSourceField().getStringValue())) {
					String paramValue = "value" + System.currentTimeMillis();
					String param = ":" + paramValue;					
					if(getSourceField().getPositionSourceType().isBefore()) {
						if(getSourceField().isFindIgnoreCase()) {
							sql = "CASE WHEN strpos(UPPER(" + fieldCol + "), UPPER(" + param + ")) > 1 THEN "
									+ "substr(" + fieldCol + ", 1, strpos(UPPER(" + fieldCol + "), UPPER(" + param + ")) - 1) ELSE NULL END";
						}	
						else {
							sql = "CASE WHEN strpos(" + fieldCol + ", " + param + ") > 1 THEN "
									+ "substr(" + fieldCol + ", 1, strpos(" + fieldCol + ", " + param + ") - 1) ELSE NULL END";
						}
					}
					else {
						if(getSourceField().isFindIgnoreCase()) {
							sql = "CASE WHEN strpos(UPPER(" + fieldCol + "), UPPER(" + param + ")) > 0 THEN "
									+ "substr(" + fieldCol + ", strpos(UPPER(" + fieldCol + "), UPPER(" + param + ")) + length(" + param + ")) ELSE NULL END";
						}	
						else {
							sql = "CASE WHEN strpos(" + fieldCol + ", " + param + ") > 0 THEN "
									+ "substr(" + fieldCol + ", strpos(" + fieldCol + ", " + param + ") + length(" + param + ")) ELSE NULL END";
						}
					}					
					parameters.put(paramValue, getSourceField().getStringValue());
				}
				else {
					sql = fieldCol;
				}
			}			
		}	
		
		if(StringUtils.hasText(sql)) {
			if(getType().isAttribute()) {
				sql = " SET " + col + " = " + sql;
			}
			else if(getType().isPeriod()) {
				String format = StringUtils.hasText(getSourceField().getDateFormat()) ? "'" + getSourceField().getDateFormat() + "'" : "'dd/MM/yyyy'";
				sql = " SET " + col + " = TO_DATE(" + sql + ", " + format + ")";
			}
			else if(getType().isMeasure()) {
				String thousandSeparator = getSourceField().getThousandSeparator();
				if(thousandSeparator != null) {
					sql = "REPLACE(" + sql + ", '" + thousandSeparator + "', '')";
				}
				String decimalSeparator = getSourceField().getDecimalSeparator();
				if(StringUtils.hasText(decimalSeparator)) {
					sql = "REPLACE(" + sql + ", '" + decimalSeparator + "', '.')";
				}							
				sql = " SET " + col + " = CAST(" + sql + " AS DECIMAL)";
			}
		}
		
		return sql;
	}
	
	
}
