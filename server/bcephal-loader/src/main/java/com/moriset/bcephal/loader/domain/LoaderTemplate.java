/**
 * 3 juin 2024 - LoaderTemplate.java
 *
 */
package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Emmanuel Emmeni
 *
 */
@Entity(name = "LoaderTemplate")
@Table(name = "BCP_LOADER_TEMPLATE")
@Data
@EqualsAndHashCode(callSuper = false)
public class LoaderTemplate extends MainObject {

	private static final long serialVersionUID = -4961591542931634183L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loader_template_seq")
	@SequenceGenerator(name = "loader_template_seq", sequenceName = "loader_template_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String repository;

	@Override
	public Persistent copy() {
		LoaderTemplate copy = new LoaderTemplate();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setDescription(getDescription());
		copy.setCode(getCode());
		copy.setRepository(getRepository());
		return copy;
	}

}
