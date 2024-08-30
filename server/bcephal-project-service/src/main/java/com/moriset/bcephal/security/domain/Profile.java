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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "Profil")
@Table(name = "BCP_SEC_PROFIL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class Profile extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3745014979225291478L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profil_seq")
	@SequenceGenerator(name = "profil_seq", sequenceName = "profil_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String code;

	@JsonIgnore
	private String client;

	@JsonIgnore
	private Long clientId;

	@Column(name = "type_")
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{profil.type.validation.null.message}")
	private ProfileType type;

	@Override
	public Persistent copy() {
		return null;
	}

}
