/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "AttributeValue")
@Table(name = "BCP_ATTRIBUTE_VALUE")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
public class AttributeValue extends Persistent {

	private static final long serialVersionUID = 5395658052058860119L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_value_seq")
	@SequenceGenerator(name = "attribute_value_seq", sequenceName = "attribute_value_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String name;

	private int position;

	@NotNull
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute")
	private Attribute attribute;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private AttributeValue parent;


	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<AttributeValue> children;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<AttributeValue> childrenListChangeHandler;

	public AttributeValue() {
		children = new ArrayList<AttributeValue>(0);
		childrenListChangeHandler = new ListChangeHandler<>(children);
	}

	public void setChildren(List<AttributeValue> values) {
		this.children = values;
		childrenListChangeHandler.setOriginalList(values);
	}

	@PostLoad
	public void initListChangeHandler() {
		for (AttributeValue value : children) {
			value.getName();
		}
		childrenListChangeHandler.setOriginalList(children);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public AttributeValue copy() {
		AttributeValue value = new AttributeValue();
		return value;
	}

}
