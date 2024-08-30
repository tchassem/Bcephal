/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Column;
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
@Entity(name = "ProfileData")
@Table(name = "BCP_SEC_PROFIL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class ProfileData extends Persistent {
	
	private static final long serialVersionUID = -1304368220817408582L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profil_seq")
	@SequenceGenerator(name = "profil_seq", sequenceName = "profil_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String name;	
	private String code;	
	private String description;		
	@JsonIgnore
	private Long clientId;
	
	@Column(name="type_")
	@Enumerated(EnumType.STRING) 
	private ProfileType type;
			

	@Override
	public Persistent copy() {
		return null;
	}

	
	
}
