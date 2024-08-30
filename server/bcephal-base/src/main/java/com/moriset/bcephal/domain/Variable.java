package com.moriset.bcephal.domain;

import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author Moriset
 *
 */
@Entity(name = "Variable")
@Table(name = "BCP_VARIABLE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Variable extends MainObject {
	
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "variable_seq")
	@SequenceGenerator(name = "variable_seq", sequenceName = "variable_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;	
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;

	private Long dimensionId;

	@Override
	public Variable copy() {		
		Variable cop = new Variable();
		cop.setCreationDate(getCreationDate());
		cop.setDataSourceId(getDataSourceId());
		cop.setDataSourceType(getDataSourceType());
		cop.setDataSourceName(getDataSourceName());
		cop.setDescription(getDescription());
		cop.setDimensionId(getDimensionId());
		cop.setDimensionType(getDimensionType());
		cop.setDocumentCount(getDocumentCount());
		cop.setGroup(getGroup());
		cop.setModificationDate(getModificationDate());
		cop.setName(getName());
		cop.setVisibleInShortcut(isVisibleInShortcut());
		return cop;
	}

}
