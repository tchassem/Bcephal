/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;

import jakarta.persistence.Entity;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SlimMeasure")
@Table(name = "BCP_MEASURE")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Measure extends Dimension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2426777047884544739L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measure_seq")
	@SequenceGenerator(name = "measure_seq", sequenceName = "measure_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Measure parent;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Measure> children;

	public Measure() {
		children = new ArrayList<Measure>(0);
	}

	public Measure(Long id) {
		this();
		setId(id);
	}

	public Measure(Long id, String name) {
		this(id);
		setName(name);
	}
	
	public Measure(Long id, String name, DataSourceType dataSourceType, Long datatSourceId) {
		this(id, name);
		setDataSourceId(datatSourceId);
		setDataSourceType(dataSourceType);
	}

	@PostLoad
	public void initListChangeHandler() {
		children.size();
	}

	@Override
	public String getFieldId() {
		return "MEASURE_" + getId();
	}

	@Override
	public String getParentId() {
		if (getParent() != null) {
			return "MEASURE_" + getParent();
		}
		return null;
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnName() {
		if(getDataSourceType().isMaterializedGrid()) {
			return "COLUMN" + getId();
		}
		if(getDataSourceType().isJoin()) {
			return "COLUMN" + getId();
		}
		return "MEASURE_" + getId();
	}
	
	@JsonIgnore
	@Override
	public String getUniverseTableColumnType() {
		return "DECIMAL (31, 14)";
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
