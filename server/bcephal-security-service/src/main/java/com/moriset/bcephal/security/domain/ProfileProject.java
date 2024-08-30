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
@Entity(name = "ProfileProject")
@Table(name = "BCP_SEC_PROFIL_PROJECT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class ProfileProject extends Persistent {

	private static final long serialVersionUID = -1080126318698692697L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_project_seq")
	@SequenceGenerator(name = "profile_project_seq", sequenceName = "profile_project_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	private Long profileId;
	
	private Long projectId;
	
	private String projectCode;
	
	private Long clientId;
	
	private int position;
	
	@Transient
	private String name;

	@Override
	public Persistent copy() {		
		return null;
	}
	
}
