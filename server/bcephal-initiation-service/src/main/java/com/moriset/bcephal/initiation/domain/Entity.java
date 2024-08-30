/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "Entity")
@Table(name = "BCP_ENTITY")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Entity extends Persistent {

	private static final long serialVersionUID = 1270255477508443179L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
	@SequenceGenerator(name = "entity_seq", sequenceName = "entity_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull(message = "{entity.name.validation.null.message}")
	@Size(min = 1, max = 255, message = "{entity.name.validation.size.message}")
	private String name;

	@NotNull
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model")
	private Model model;

	private boolean visibleByAdminOnly;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = { jakarta.persistence.CascadeType.REFRESH,
			jakarta.persistence.CascadeType.MERGE }, fetch = FetchType.LAZY, mappedBy = "entity")
	private List<Attribute> attributes;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<Attribute> attributeListChangeHandler;

	public Entity() {
		attributes = new ArrayList<Attribute>(0);
		attributeListChangeHandler = new ListChangeHandler<>(attributes);
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
		attributeListChangeHandler.setOriginalList(attributes);
	}

	@PostLoad
	public void initListChangeHandler() {
		for (Attribute attribute : attributes) {
			attribute.getName();
		}
		attributeListChangeHandler.setOriginalList(attributes);
	}

//	@JsonIgnore	
//	public List<Attribute> getAttributes(){
//		return attributes;
//	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
