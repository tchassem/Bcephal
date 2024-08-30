/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class ClientBaseObject extends MainObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8268836128595291806L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
	@SequenceGenerator(name = "client_seq", sequenceName = "client_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@NotNull(message = "{client.code.validation.null.message}") 
	@Size(min = 1, max = 50, message = "{client.code.validation.size.message}")
	private String clientId;
	
	private String code;
	
	private String description;
			
	private String secret;
	
	private String defaultLanguage;
	
	private boolean defaultClient;
	
	private int maxUser;
		
	@Enumerated(EnumType.STRING) 
	@NotNull(message = "{client.nature.validation.null.message}") 
	private ClientNature nature;
	
	@Column(name="type_")
	@Enumerated(EnumType.STRING) 
	@NotNull(message = "{client.type.validation.null.message}") 
	private ClientType type;
	
	@Enumerated(EnumType.STRING) 
	@NotNull(message = "{client.status.validation.null.message}")
	private ClientStatus status;
	
	@Embedded
	private Address address;
	
	

	@Transient 
	private ListChangeHandler<ClientFunctionality> functionalityListChangeHandler;
	
	@Transient 
	private ListChangeHandler<Nameable> profileListChangeHandler;
	
	
	public ClientBaseObject() {
		this.functionalityListChangeHandler = new ListChangeHandler<ClientFunctionality>();
		this.profileListChangeHandler = new ListChangeHandler<Nameable>();
	}
	
		
	@JsonIgnore
	public boolean isEnabled() {
		return status != null && status.isEnabled();
	}
	

	@Override
	public Persistent copy() {
		return null;
	}

	

}
