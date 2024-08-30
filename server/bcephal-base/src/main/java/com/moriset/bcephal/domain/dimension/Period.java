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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SlimPeriodName")
@Table(name = "BCP_PERIOD_NAME")
@Data
@EqualsAndHashCode(callSuper = false)
public class Period extends Dimension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3135577925306713404L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "period_name_seq")
	@SequenceGenerator(name = "period_name_seq", sequenceName = "period_name_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Period parent;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Period> children;

	public Period() {
		children = new ArrayList<Period>(0);
	}

	public Period(Long id) {
		this();
		setId(id);
	}

	public Period(Long id, String name) {
		this(id);
		setName(name);
	}
	
	public Period(Long id, String name, DataSourceType dataSourceType, Long datatSourceId) {
		this(id, name);
		setDataSourceId(datatSourceId);
		setDataSourceType(dataSourceType);
	}

	@PostLoad
	public void initListChangeHandler() {
		children.size();
		children.forEach(m -> m.getName());
	}

	@Override
	public String getFieldId() {
		return "PERIOD_" + getId();
	}

	@Override
	public String getParentId() {
		if (getParent() != null) {
			return "PERIOD_" + getParent();
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
		return "PERIOD_" + getId();
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnType() {
		return "DATE";
	}

}
