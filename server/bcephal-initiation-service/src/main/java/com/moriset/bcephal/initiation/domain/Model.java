/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * A <code>Model</code> is equivalent to a folder containing a list of entities
 * (<code>Entity</code>).
 * 
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "Model")
@Table(name = "BCP_MODEL")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Model extends Persistent {

	private static final long serialVersionUID = 5039860845446698510L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "model_seq")
	@SequenceGenerator(name = "model_seq", sequenceName = "model_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	/**
	 * The name
	 */
	@NotNull(message = "{model.name.validation.null.message}")
	@Size(min = 1, max = 255, message = "{model.name.validation.size.message}")
	private String name;

	/**
	 * The position
	 */
	private int position;

	/**
	 * Is this model visible in shortcut?
	 */
	private boolean visibleInShortcut;

	private boolean visibleByAdminOnly;

	private String diagramXml;

	/**
	 * Creation date time.
	 */
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	/**
	 * Last modification date time.
	 */
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<Entity> entities;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<Entity> entityListChangeHandler;

	public Model() {
		visibleInShortcut = true;
		entities = new ArrayList<Entity>(0);
		entityListChangeHandler = new ListChangeHandler<>(entities);
	}
	
	public Model(Long id) {
		this();
		setId(id);
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
		entityListChangeHandler.setOriginalList(entities);
	}

	@PostLoad
	public void initListChangeHandler() {
		entities.size();
		entityListChangeHandler.setOriginalList(entities);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Model copy() {
		Model model = new Model();
		model.setName(name);
		model.setPosition(position);
		model.setVisibleInShortcut(visibleInShortcut);
		return model;
	}

}
