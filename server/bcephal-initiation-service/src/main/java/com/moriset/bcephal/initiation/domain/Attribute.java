/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "Attribute")
@Table(name = "BCP_ATTRIBUTE")
@Data
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Attribute extends Dimension {

	private static final long serialVersionUID = 6994140632034339997L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_seq")
	@SequenceGenerator(name = "attribute_seq", sequenceName = "attribute_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String defaultValue;

	private boolean declared;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity")
	private Entity entity;


	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Attribute parent;


	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<Attribute> childrenListChangeHandler;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Attribute> children;

	@ToString.Exclude @EqualsAndHashCode.Exclude
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
		this.setId(id);
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
		for (Attribute attribute : children) {
			attribute.getName();
			// getEntity().getAttributeListChangeHandler().getOriginalList().remove(attribute);
		}
		childrenListChangeHandler.setOriginalList(children);
		for (AttributeValue value : values) {
			value.getName();
		}
		valueListChangeHandler.setOriginalList(values);
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnName() {
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

	@Override
	public Attribute copy() {
		return null;
	}

}
