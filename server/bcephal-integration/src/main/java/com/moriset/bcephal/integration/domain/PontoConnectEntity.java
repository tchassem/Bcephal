package com.moriset.bcephal.integration.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "PontoConnectEntity")
@Table(name = "BCP_SEC_PONTO")
@Data
@EqualsAndHashCode(callSuper = false)
public class PontoConnectEntity extends ConnectEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5285706075230382429L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ponto_entity_seq")
	@SequenceGenerator(name = "ponto_entity_seq", sequenceName = "ponto_entity_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String clientApplicationId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "clientTls")
	private SslEntity clientTls;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "clientTlsCa")
	private SslEntity clientTlsCa;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "clientSignature")
	private SslEntity clientSignature;

	@JsonIgnore
	private String accountId;
	
	private String organisationId;
	private String organisationSecret;
	
	private String apiEndpoint;
	
	@JsonIgnore
	@Transient
	private List<String> accountIds;

	
	
	@PostLoad
	public void init() {
		if(StringUtils.hasText(accountId)) {
			accountIds = Arrays.asList(accountId.split(";:+;;&&;;"));
		}
	}
	
	 @PrePersist @PreUpdate
	public void buildAccount() {
		if(accountIds != null && accountIds.size() > 0) {
			accountId = null;
			 accountIds.forEach(x->
			{
				if(StringUtils.hasText(accountId)) {				
					accountId += ";:+;;&&;;" + x;
				}else {
					accountId = x;
				}
			});
		}
	}
	
	public PontoConnectEntity() {
		super();
		accountIds = new ArrayList<>();
	}
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
}
