/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
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
 * @author Moriset
 *
 */
@Entity(name = "ProfileUser")
@Table(name = "BCP_SEC_PROFIL_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class ProfileUser extends Persistent {

	private static final long serialVersionUID = 1315318055773056810L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_user_seq")
	@SequenceGenerator(name = "profile_user_seq", sequenceName = "profile_user_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	private Long profileId;
	
	private Long userId;
	
	private Long clientId;
	
	public ProfileUser(Long profileId, Long userId, Long clientId) {
		this.profileId = profileId;
		this.userId = userId;
		this.clientId = clientId;
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
