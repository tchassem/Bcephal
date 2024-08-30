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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Right")
@Table(name = "BCP_SEC_RIGHT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class Right extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2163073385896971841L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "right_seq")
	@SequenceGenerator(name = "right_seq", sequenceName = "right_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@NotNull(message = "{right.functionality.validation.null.message}") 
	@Size(min = 5, max = 255, message = "{right.functionality.validation.size.message}")
	private String functionality;
	
	@NotNull(message = "{right.level.validation.null.message}") 
	@Enumerated(EnumType.STRING)
	private RightLevel level;
	
	/**
	 * User ID
	 */
	@JsonIgnore
	private Long userId;
	
	/**
	 * Profile ID
	 */
	@JsonIgnore
	private Long profileId;
	
	@JsonIgnore
	private String projectCode;
	
	
	
	private Long objectId;
	
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
