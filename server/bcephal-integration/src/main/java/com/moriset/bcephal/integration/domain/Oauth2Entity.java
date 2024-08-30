package com.moriset.bcephal.integration.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Oauth2Entity")
@Table(name = "BCP_SEC_OAUTH2_PARAMS")
@Data
@EqualsAndHashCode(callSuper = false)
public class Oauth2Entity extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8240744607212079310L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth2_entity_seq")
	@SequenceGenerator(name = "oauth2_entity_seq", sequenceName = "oauth2_entity_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String redirectUrlBase;
	private String clientId;
	private String clientSecret;
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
