/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "RightData")
@Table(name = "BCP_SEC_RIGHT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class RightData extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2163073385896971841L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "right_seq")
	@SequenceGenerator(name = "right_seq", sequenceName = "right_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	private String functionality;
	
	@Enumerated(EnumType.STRING)
	private RightLevel level;	
	
	/**
	 * Profile ID
	 */
	@JsonIgnore
	private Long profileId;	
	
	private Long objectId;
	
	@JsonIgnore
	private String projectCode;
	
	@JsonIgnore
	public String getRole() {
		return String.format("ROLE_{}-{}", functionality, level.name());
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
