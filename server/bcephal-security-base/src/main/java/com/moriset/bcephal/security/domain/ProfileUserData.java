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
@Entity(name = "ProfileUserData")
@Table(name = "BCP_SEC_PROFIL_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class ProfileUserData extends Persistent {
	
	private static final long serialVersionUID = 6378803304598093342L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_user_seq")
	@SequenceGenerator(name = "profile_user_seq", sequenceName = "profile_user_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	private Long profileId;
	
	private Long userId;
	
	private Long clientId;

	@Override
	public Persistent copy() {
		return null;
	}
	
}
