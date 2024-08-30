/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "UserData")
@Table(name = "BCP_SEC_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserData extends Persistent {

	private static final long serialVersionUID = 582360624638828422L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long clientId;
	private String username;
	private boolean enabled;
	private boolean emailVerified;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private String defaultLanguage;

	@Enumerated(EnumType.STRING)
	private ProfileType type;


	@Transient
	private ListChangeHandler<Nameable> profileListChangeHandler = new ListChangeHandler<>();

	@JsonIgnore
	public boolean isAdministrator() {
		return getType() != null && getType().isAdministrator();
	}

	@JsonIgnore
	public boolean isSuperUser() {
		return getType() != null && getType().isSuperUser();
	}

	@JsonIgnore
	public boolean isAdministratorOrSuperUser() {
		return isAdministrator() || isSuperUser();
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
