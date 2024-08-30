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
@Entity(name = "UserInfo")
@Table(name = "BCP_SEC_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class UserInfo extends Persistent {
	
	private static final long serialVersionUID = -2673147375416406276L;

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore	
	private Long clientId;

	private String username;
	private String name;
	private String lastName;
	private String firstName;
	private String defaultLanguage;
	
	@Enumerated(EnumType.STRING)
	private ProfileType type;
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	
}
