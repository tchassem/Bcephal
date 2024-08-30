package com.moriset.bcephal.etl.domain;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "EmailAccount")
@Table(name = "bcp_email_account")
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EmailAccount extends Persistent {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_account_seq")
	@SequenceGenerator(name = "email_account_seq", sequenceName = "email_account_seq", initialValue = 1, allocationSize = 1)
	Long id;
	
	@Column(name = "user_email")
	String userName;
	String serve_host;
	String server_port;
	@JsonIgnore
	String propertiesImpl;
	
	String password;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "emailAccount",cascade = CascadeType.REFRESH)
	List<EmailFilter> emailFilters;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient 
	private ListChangeHandler<EmailFilter> emailFiltersListChangeHandler;
	
	@Transient
	Map<String, String> properties;
	
	public  EmailAccount() {
		properties = new HashMap<>();
		emailFiltersListChangeHandler = new ListChangeHandler<EmailFilter>();
	}
	
	@PostLoad
	public void init() {
		if(emailFilters!= null) {
			emailFilters.forEach(x->{});
			emailFiltersListChangeHandler.setOriginalList(emailFilters);
		}
		if(StringUtils.hasText(propertiesImpl)) {
			try {
				properties = new ObjectMapper().readValue(propertiesImpl, new TypeReference<Map<String, String>>() {
				});
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
