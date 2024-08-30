/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.IPersistent;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@jakarta.persistence.Entity(name = "SlimModel")
@Table(name = "BCP_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class Model implements IPersistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8655865529044188856L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "model_seq")
	@SequenceGenerator(name = "model_seq", sequenceName = "model_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String name;

	private int position;

	private boolean visibleByAdminOnly;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<Entity> entities;

	public Model() {
		entities = new ArrayList<Entity>(0);
	}
	
	public Model(String name) {
		this();
		setName(name);
	}

	@PostLoad
	public void initListChangeHandler() {
		entities.size();
	}

	public String getFieldId() {
		return "MODEL_" + getId();
	}

	public String getParentId() {
		return null;
	}

}
