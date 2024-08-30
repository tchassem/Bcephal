/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;
import com.moriset.bcephal.service.SpotQueryBuilder;
import com.moriset.bcephal.service.filters.ISpotService;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinColumnProperties")
@Table(name = "BCP_JOIN_COLUMN_PROPERTIES")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnProperties extends Persistent {
	
	private static final long serialVersionUID = 6655424341061128634L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_properties_seq")
	@SequenceGenerator(name = "join_column_properties_seq", sequenceName = "join_column_properties_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long columnId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @jakarta.persistence.JoinColumn(name = "field")
	private JoinColumnField field;
		
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "propertiesId")
	private List<JoinColumnConcatenateItem> concatenateItems;	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinColumnConcatenateItem> concatenateItemListChangeHandler;
		
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "propertiesId")
	private List<JoinColumnCalculateItem> calculateItems;	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinColumnCalculateItem> calculateItemListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "propertiesId")
	private List<JoinColumnConditionItem> conditionItems;	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinColumnConditionItem> conditionItemListChangeHandler;
	    
	
	public JoinColumnProperties() {
		this.concatenateItems = new ArrayList<>();
		this.calculateItems = new ArrayList<>();
		this.conditionItems = new ArrayList<>();
		this.concatenateItemListChangeHandler = new ListChangeHandler<JoinColumnConcatenateItem>();
		this.calculateItemListChangeHandler = new ListChangeHandler<JoinColumnCalculateItem>();
		this.conditionItemListChangeHandler = new ListChangeHandler<JoinColumnConditionItem>();
	}
	
	public void setConcatenateItems(List<JoinColumnConcatenateItem> concatenateItems) {
		this.concatenateItems = concatenateItems;
		concatenateItemListChangeHandler.setOriginalList(concatenateItems);
	}
	
	public void setCalculateItems(List<JoinColumnCalculateItem> calculateItems) {
		this.calculateItems = calculateItems;
		calculateItemListChangeHandler.setOriginalList(calculateItems);
	}
	
	public void setConditionItems(List<JoinColumnConditionItem> conditionItems) {
		this.conditionItems = conditionItems;
		conditionItemListChangeHandler.setOriginalList(conditionItems);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		calculateItems.forEach(x->{});
		concatenateItems.forEach(x->{});
		conditionItems.forEach(x->{});
		calculateItemListChangeHandler.setOriginalList(calculateItems);
		concatenateItemListChangeHandler.setOriginalList(concatenateItems);
		conditionItemListChangeHandler.setOriginalList(conditionItems);
	}
	
	
	@JsonIgnore
	public String getCustomCol(boolean withalias, String aliasCol, Map<String, Object> parameters, Join join, String measureFunction, ISpotService spotService, Map<String, Object> variableValues) {
		//String aliasCol = withalias ? "column_" + RandomUtils.nextInt(0, 100000) + "_" + System.currentTimeMillis() : "";
		String col = "null";
		if(getField() != null) {
			if(getField().isFree()) {
				if(getField().isVariable() && variableValues != null) {	
					Object val = getField().getVariableValue(variableValues);
					if(val != null) {
						if(getField().isAttribute()) {
							col = "'" + val + "'";
						}
						else if(getField().isMeasure()) {
							col = "" + val + "";
						}
						else if(getField().isPeriod()) {
							if(val instanceof VariableIntervalPeriod) {
								col = "'" + new SimpleDateFormat("yyyy-MM-dd").format(((VariableIntervalPeriod)val).getStart()) + "'";
							}
							if(val instanceof Date) {
								col = "'" + new SimpleDateFormat("yyyy-MM-dd").format((Date)val) + "'";
							}
						}
					}
				}
				else {
					Object value = getField().getFreeValue();
					if(value != null) {
						if(withalias) {
							String key = aliasCol;
							parameters.put(key, value);
							col = ":" + key;
						}
						else {
							String key = "column_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
							parameters.put(key, value);
							col = ":" + key;
						}
					}
				}
			}
			else if(getField().isColumn()) {
				String part = getField().getCustomColumnCol(measureFunction, true);	
				if(part != null) {
					col = part;
				}				
			}
			else if(getField().isCalculate()) {					
				col = getCustomCalculateCol(join, parameters, measureFunction, spotService, variableValues);	
			}
			else if(getField().isConcatenate()) {					
				col = getCustomConcatenateCol(join, parameters, measureFunction, spotService, variableValues);	
			}
			else if(getField().isCondition()) {					
				col = getCustomConditionCol(join, parameters, measureFunction, spotService, variableValues);	
			}
			else if(getField().isCopy()) {					
				if(getField().getColumnId() != null && join != null) {
					for (JoinColumn column : join.getColumns()) {
						if (column.getId() != null && column.getId().equals(getField().getColumnId())) {
							//col = column.getDbColName(true, false);
							col = column.isCustom() ? column.getCustomCol(false, parameters, join, spotService, variableValues) : column.getDbColName(true, false);
							break;
						}							
					}
				}
			}
			else if(getField().isSpot()) {	
				List<UniverseFilter> filters = new ArrayList<>();
				if(getField().getSpot() != null && spotService != null) {
					filters = spotService.buildSpotReportFilter(getField().getSpot());
				}
				col = new SpotQueryBuilder(getField().getSpot(), filters).buildQuery();	
				col = StringUtils.hasText(col) ? ("(" + col + ")") : col;
			}
			else if(getField().isSequence()) {		
				col = "''";	
			}
			else if(getField().isVariable() && variableValues != null) {	
				Object val = variableValues.get(getField().getStringValue());
				if(val != null) {
					if(getField().isAttribute()) {
						col = "'" + val + "'";
					}
					else if(getField().isMeasure()) {
						col = "" + val + "";
					}
					else if(getField().isPeriod() && getField().getDateValue() != null) {
						getField().getDateValue().setVariableName(getField().getStringValue());
						val = getField().getDateValue().buildDynamicDate(variableValues);						
						if(val instanceof VariableIntervalPeriod) {
							col = "'" + new SimpleDateFormat("yyyy-MM-dd").format(((VariableIntervalPeriod)val).getStart()) + "'";
						}
						if(val instanceof Date) {
							col = "'" + new SimpleDateFormat("yyyy-MM-dd").format((Date)val) + "'";
						}
					}
				}
			}
		}		
		return col + (withalias ? " AS " + aliasCol : "");
	}
	
	@JsonIgnore
	private String getCustomCalculateCol(Join join, Map<String, Object> parameters, String measureFunction, ISpotService spotService, Map<String, Object> variableValues) {
		String col = "";
		if(getField() != null && getField().isCalculate()) {
			for (JoinColumnCalculateItem item : getCalculateItems()) {
				String part = "";
				if(item.getField() != null) {
					if(item.getField().isFree()) {
						Object value = item.getField().getFreeValue();
						if(value == null) {
							value = BigDecimal.ZERO;							
						}
						String key = "Param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
						parameters.put(key, value);
						part = ":" + key;
					}
					else if(item.getField().isColumn()) {
						part = item.getField().getCustomColumnCol(measureFunction, true);	
						if(part == null) {
							part = "0";
						}
					}
					else if(item.getField().isCopy()) {
						part = item.getField().getCustomCopyCol(join, parameters, spotService, variableValues);	
						if(part == null) {
							part = "0";
						}
					}
				}	
				String sign = item.getSign() != null ? " " + item.getSign() + " " : "";
				String op = item.getOpeningBracket() != null ? item.getOpeningBracket() : "";
				String cp = item.getClosingBracket() != null ? item.getClosingBracket() : "";
				col += sign + op + part + cp;	
			}
		}
		return StringUtils.hasText(col) ? col : "0";
	}
	
	@JsonIgnore
	private String getCustomConcatenateCol(Join join, Map<String, Object> parameters, String measureFunction, ISpotService spotService, Map<String, Object> variableValues) {
		String col = "";
		if(getField() != null && getField().isConcatenate()) {
			String sign = "";
			for (JoinColumnConcatenateItem item : getConcatenateItems()) {
				String part = "";
				if(item.getField() != null) {
					if(item.getField().isFree()) {
						Object value = item.getField().getFreeValue();
						if(value == null) {
							value = "";							
						}
						String key = "Param_" + RandomUtils.nextInt(100000) + "_" + System.currentTimeMillis();
						parameters.put(key, value);
						part = ":" + key;
					}
					else if(item.getField().isColumn()) {
						part = item.getField().getCustomColumnCol(measureFunction, true);	
						if(part == null) {
							part = "''";
						}
					}
					else if(item.getField().isCopy()) {
						part = item.getField().getCustomCopyCol(join, parameters, spotService, variableValues);	
						if(part == null) {
							part = "''";
						}
					}
				}	
				col += (StringUtils.hasText(col) ? sign : "") + part;	
				sign = " || ";
			}
		}
		return StringUtils.hasText(col) ? col : "null";
	}
	
	@JsonIgnore
	private String getCustomConditionCol(Join join, Map<String, Object> parameters, String measureFunction, ISpotService spotService, Map<String, Object> variableValues) {
		String col = "";
		if(getField() != null && getField().isCondition()) {
			String coma = "";
			for (JoinColumnConditionItem item : getConditionItems()) {
				String part = item.asSql(join, item, parameters, measureFunction, getField(), variableValues);
				if(part != null && StringUtils.hasText(part.trim())) {
					col += coma + part;
					coma = " ";
				}	
			}
		}
		if(col != null && StringUtils.hasText(col.trim())) {
			col = "CASE " + col + " END";
		}	
		return StringUtils.hasText(col) ? col : "0";
	}
	
	
	@JsonIgnore
	public String getGroupByCols(Join join) {
		String col = "";
		if(getField() != null) {
			if(getField().isColumn() || getField().isCopy()) {
				col = getField().getGroupByCols(join);	
			}
			else if(getField().isCalculate()) {					
				String coma = "";
				for (JoinColumnCalculateItem item : getCalculateItems()) {
					String part = item.getField().getGroupByCols(join)	;
					if(part != null && StringUtils.hasText(part.trim())) {
						col += coma + part;
						coma = ", ";
					}				
				}
			}
			else if(getField().isConcatenate()) {					
				String coma = "";
				for (JoinColumnConcatenateItem item : getConcatenateItems()) {
					String part = item.getField().getGroupByCols(join)	;
					if(part != null && StringUtils.hasText(part.trim())) {
						col += coma + part;
						coma = ", ";
					}				
				}	
			}
			else if(getField().isCondition()) {			
				String coma = "";
				for (JoinColumnConditionItem item : getConditionItems()) {
					String part = item.getGroupByCols(join)	;
					if(part != null && StringUtils.hasText(part.trim())) {
						col += coma + part;
						coma = ", ";
					}				
				}
			}
		}
		return StringUtils.hasText(col) ? col : null;
	}
	

	@Override
	public JoinColumnProperties copy() {
		JoinColumnProperties copy = new JoinColumnProperties();
//		copy.setColumnId(getColumnId());
		
		for (JoinColumnConcatenateItem item : this.getConcatenateItemListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinColumnConcatenateItem copyField = (JoinColumnConcatenateItem)item.copy();
			copy.getConcatenateItemListChangeHandler().addNew(copyField);
		}
		for (JoinColumnCalculateItem item : this.getCalculateItemListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinColumnCalculateItem copyField = (JoinColumnCalculateItem)item.copy();
			copy.getCalculateItemListChangeHandler().addNew(copyField);
		}
		for (JoinColumnConditionItem item : this.getConditionItemListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinColumnConditionItem copyField = (JoinColumnConditionItem)item.copy();
			copy.getConditionItemListChangeHandler().addNew(copyField);
		}
        if(getField() != null) {
        	JoinColumnField joinColumnField = (JoinColumnField)getField().copy(); 
    		copy.setField(joinColumnField);
        }
        
		return copy;
	}
	
	public boolean updateCopy(Join copy, HashMap<Long, JoinColumn> columns) {
		boolean updated = false;
		if(getField().isCopy()) {				
			JoinColumn col = columns.get(getField().getColumnId());
			getField().setColumnId(col != null ? col.getId() : null);
			updated = true;
		}
		else if(getField().isConcatenate()) {
			setConcatenateItems(this.getConcatenateItemListChangeHandler().getItems());
			for (JoinColumnConcatenateItem item : this.getConcatenateItems()) {
				if (item == null) continue;
				if(item.getField() != null) {
					boolean res = item.getField().updateCopy(copy, columns);
					if(res) {
						getConcatenateItemListChangeHandler().addUpdated(item);
						updated = res;
					}
				}				
			}
		}
		else if(getField().isCalculate()) {
			setCalculateItems(this.getCalculateItemListChangeHandler().getItems());
			for (JoinColumnCalculateItem item : this.getCalculateItems()) {
				if (item == null) continue;
				if(item.getField() != null) {
					boolean res = item.getField().updateCopy(copy, columns);
					if(res) {
						getCalculateItemListChangeHandler().addUpdated(item);
						updated = res;
					}
				}
			}
		}
		else if(getField().isCondition()) {
			setConditionItems(this.getConditionItemListChangeHandler().getItems());
			for (JoinColumnConditionItem item : this.getConditionItems()) {
				if (item == null) continue;
				boolean res = item.updateCopy(copy, columns);
				if(res) {
					getConditionItemListChangeHandler().addUpdated(item);
					updated = res;
				}
			}
		}
		return updated;
	}

}
