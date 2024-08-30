/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;

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
@jakarta.persistence.Entity(name = "SlimAttributevalue")
@Table(name = "BCP_ATTRIBUTE_VALUE")
@Data
@EqualsAndHashCode(callSuper = false)
public class AttributeValue implements IPersistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9170179078210172073L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_value_seq")
	@SequenceGenerator(name = "attribute_value_seq", sequenceName = "attribute_value_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String name;

	private int position;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private AttributeValue parent;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute")
	private Attribute attribute;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<AttributeValue> children;

	public AttributeValue() {
		children = new ArrayList<AttributeValue>(0);
	}

	@PostLoad
	public void initListChangeHandler() {
		children.size();
		children.forEach(m ->{});
	}

	public String getFieldId() {
		return "VALUE_" + getId();
	}

	public String getParentId() {
		if (getParent() != null) {
			return "VALUE_" + getParent();
		}
		if (getAttribute() != null) {
			return getAttribute().getFieldId();
		}
		return null;
	}

	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}

}
