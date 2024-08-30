package com.moriset.bcephal.domain.dimension;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonSubTypes({ @Type(value = Spot.class, name = "Spot") })
@Entity(name = "Spot")
@Table(name = "BCP_SPOT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Spot extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5221324852008733672L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_seq")
	@SequenceGenerator(name = "spot_seq", sequenceName = "spot_seq", initialValue = 1, allocationSize = 1)
	private Long id;
		
	private Long measureId;
	
	@Enumerated(EnumType.STRING)
	private MeasureFunctions measureFunction;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "filter")
	private UniverseFilter filter;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType gridType;
	
	private Long gridId;
	
	@Transient
	private String gridName;
	
	
	public Spot() {
		this.measureFunction = MeasureFunctions.SUM;
		this.gridType = DataSourceType.UNIVERSE;
		filter = new UniverseFilter();
	}

	@JsonIgnore
	public String getUniverseTableColumnName() {
		if(!gridType.isMaterializedGrid()) {
			return new Measure(measureId).getUniverseTableColumnName();
		}else {
			return "column" + measureId;
		}
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
			if(gridId != null && filters != null && filters.size() > 0) {	
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

	@Override
	public Spot copy() {	
		Spot copy = new Spot();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setDescription(getDescription());
		copy.setFilter(filter != null ? filter.copy() : null);
		copy.setGridId(gridId);
		copy.setMeasureFunction(measureFunction);
		copy.setMeasureId(measureId);
		copy.setGridType(getGridType());
		return copy;
	}

}


