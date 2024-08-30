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

@jakarta.persistence.Entity(name = "SlimEntity")
@Table(name = "BCP_ENTITY")
@Data
@EqualsAndHashCode(callSuper = false)
public class Entity implements IPersistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557539388660452742L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
	@SequenceGenerator(name = "entity_seq", sequenceName = "entity_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String name;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model")
	private Model model;

	private boolean visibleByAdminOnly;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "entity")
	private List<Attribute> attributes;

	public Entity() {
		attributes = new ArrayList<Attribute>(0);
	}
	
	public Entity(String name) {
		this();
		setName(name);
	}

	@PostLoad
	public void initListChangeHandler() {
		attributes.size();
	}

	public String getFieldId() {
		return "ENTITY_" + getId();
	}

	public String getParentId() {
		if (getModel() != null) {
			return getModel().getFieldId();
		}
		return null;
	}

}
