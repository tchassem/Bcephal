/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;

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
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "SlimAttribute")
@Table(name = "BCP_ATTRIBUTE")
@Data
@EqualsAndHashCode(callSuper = true)
public class Attribute extends Dimension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7990672440738847197L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_seq")
	@SequenceGenerator(name = "attribute_seq", sequenceName = "attribute_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity")
	private Entity entity;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Attribute parent;
	
	private boolean declared;

	private Long incremantalValue;

	@Transient
	private ListChangeHandler<Attribute> childrenListChangeHandler;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Attribute> children;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "attribute")
	private List<AttributeValue> values;

	@Transient
	private ListChangeHandler<AttributeValue> valueListChangeHandler;

	public Attribute() {
		values = new ArrayList<AttributeValue>(0);
		valueListChangeHandler = new ListChangeHandler<>(values);

		children = new ArrayList<Attribute>(0);
		childrenListChangeHandler = new ListChangeHandler<>(children);
	}

	public Attribute(Long id) {
		this();
		setId(id);
	}

	public Attribute(Long id, String name) {
		this(id);
		setName(name);
	}
	
	public Attribute(Long id, String name, DataSourceType dataSourceType, Long datatSourceId) {
		this(id, name);
		setDataSourceId(datatSourceId);
		setDataSourceType(dataSourceType);
	}

	public void setValues(List<AttributeValue> values) {
		this.values = values;
		valueListChangeHandler.setOriginalList(values);
	}

	public void setChildren(List<Attribute> children) {
		this.children = children;
		childrenListChangeHandler.setOriginalList(children);
	}

	@PostLoad
	public void initListChangeHandler() {
		children.size();
		values.size();
		for (Attribute attribute : children) {
			attribute.getName();
		}
		childrenListChangeHandler.setOriginalList(children);
		for (AttributeValue value : values) {
			value.getName();
		}
		valueListChangeHandler.setOriginalList(values);
	}

	@Override
	public String getFieldId() {
		return "ATTRIBUTE_" + getId();
	}

	@Override
	public String getParentId() {
		if (getParent() != null) {
			return "ATTRIBUTE_" + getParent();
		}
		if (getEntity() != null) {
			return getEntity().getFieldId();
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
		return "ATTRIBUTE_" + getId();
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnType() {
		return "TEXT";
	}

	@Override
	public String toString() {
		return getName();
	}

}
