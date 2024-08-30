package com.moriset.bcephal.integration.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "SslEntity")
@Table(name = "BCP_SEC_SSL")
@Data
@EqualsAndHashCode(callSuper = false)
public class SslEntity  extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3847646761469549651L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ssl_entity_seq")
	@SequenceGenerator(name = "ssl_entity_seq", sequenceName = "ssl_entity_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	private String certificateId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
//	@Lob
//	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "certificatePath")
	private String certificate;
	
	@Transient
	private String certificatePath;
	
	private String privateKeyPassphrase;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
//	@Lob
//	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "privateKeyPath")
	private String privateKey;
	
	@Transient
	private String privateKeyPath;
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
}
