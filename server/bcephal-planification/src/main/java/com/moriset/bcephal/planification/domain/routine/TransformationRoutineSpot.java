package com.moriset.bcephal.planification.domain.routine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;

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

@Entity(name = "TransformationRoutineSpot")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_SPOT")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineSpot extends Persistent {

	private static final long serialVersionUID = -2600381515624525108L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_spot_seq")
	@SequenceGenerator(name = "transformation_routine_spot_seq", sequenceName = "transformation_routine_spot_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient @JsonIgnore 
//	@ManyToOne
//	@JoinColumn(name = "filter")
	private UniverseFilter filter;
	 
	private Long measureId;
	 
	@Enumerated(EnumType.STRING)
	private MeasureFunctions measureFunction;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "spotId")
	private List<TransformationRoutineSpotCondition> conditions;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient 
	private ListChangeHandler<TransformationRoutineSpotCondition> conditionListChangeHandler;
	
	@Transient @JsonIgnore 
	private AbstractSmartGrid<?> grid;
	
	@Transient @JsonIgnore 
	private DataSourceType sourceType;
	
	
	public TransformationRoutineSpot() {
		this.measureFunction = MeasureFunctions.SUM;
		this.dataSourceType = JoinGridType.MATERIALIZED_GRID;
		filter = new UniverseFilter();
		this.conditionListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setConditions(List<TransformationRoutineSpotCondition> conditions) {
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
		Collections.sort(getConditions(), new Comparator<TransformationRoutineSpotCondition>() {
			public int compare(TransformationRoutineSpotCondition item1, TransformationRoutineSpotCondition item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
	}
	
	
	@JsonIgnore
	public String buildQuery(String tableName, List<UniverseFilter> filters) {
		String sql = null;
		if(this.measureId != null) {
			this.measureFunction = this.measureFunction != null ? this.measureFunction : MeasureFunctions.SUM;
			sql = "SELECT " + this.measureFunction.code + "(" + getUniverseTableColumnName() + ") FROM " + tableName;
			String filterSql = null;
			if(filter != null) {
			    filterSql = filter.toSql();
				if(StringUtils.hasText(filterSql)) {
					sql += " WHERE " + filterSql;
				}
			}
			if(dataSourceId != null && filters != null && filters.size() > 0) {	
				String filterSqlSub = filterSql;
				String operator = StringUtils.hasText(filterSqlSub) ? " AND " : " WHERE ";
				for(UniverseFilter filter_ : filters) {
					filterSqlSub = filter_.toSql();
					if(StringUtils.hasText(filterSqlSub)) {
						sql += String.format(" %s (%s) ",operator, filterSqlSub);
						operator = " AND ";
					}
				}
			}
		}		
		return sql;
	}
	
	@JsonIgnore
	public String getUniverseTableColumnName() {
		if(!dataSourceType.isMaterializedGrid()) {
			return new Measure(measureId).getUniverseTableColumnName();
		}else {
			return "column" + measureId;
		}
	}

	@Override
	public TransformationRoutineSpot copy() {
		TransformationRoutineSpot copy = new TransformationRoutineSpot();
		copy.setDataSourceId(dataSourceId);
		copy.setDataSourceType(dataSourceType);
		copy.setFilter(filter != null ? filter.copy() : null);
		copy.setMeasureFunction(measureFunction);
		copy.setMeasureId(measureId);
		for(TransformationRoutineSpotCondition condition : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(condition.copy());
		}
		return copy;
	}

}
