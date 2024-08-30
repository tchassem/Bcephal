package com.moriset.bcephal.integration.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class ConnectEntity extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3033867807179144717L;

	private String name;
	
	private Long clientId;
    
	private Long projectId;
   
	private String projectCode;

	private String authorizationUrl;
	
	private String outPath;
	
	private String emails;
	
	private String messages;
	
	@JsonIgnore
	private String accessToken;
	@JsonIgnore
    private String refreshToken;
	@JsonIgnore
    private String tokenType;
	@JsonIgnore
    private int expiresIn;
	@JsonIgnore
    private String scope;
    
	private Boolean active;
	
	private Boolean scheduled;

	private String cronExpression;
	
	private Integer pageSize;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Date startDate;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "connectEntity")
	private List<EntityColumn> columns;

	@Transient
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ListChangeHandler<EntityColumn> columnListChangeHandler;
	
	@Transient
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<String> accountColumns;
	
	@Transient
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<String> transactionColumns;
	
	@Transient
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<String> financialInstitutionColumns;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "oauth2")
	private Oauth2Entity oauth2;
	
	@Enumerated(EnumType.STRING)
	IntegrationEntityType type;
	

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	/**
	 * <p style="margin-top: 0">
	 * Last modification date time
	 * </p>
	 */
	// @Version
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	/**
	 * Default constructor.
	 */
	public ConnectEntity() {
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
		type = IntegrationEntityType.PONTO_CONNECT;
		this.columnListChangeHandler = new ListChangeHandler<EntityColumn>();
		this.columnListChangeHandler = new ListChangeHandler<EntityColumn>();
		active = true;
		pageSize = 50;
		transactionColumns = new ArrayList<>();
		accountColumns = new ArrayList<>();
		financialInstitutionColumns = new ArrayList<>();
	}
	
	@PostLoad
	public void initListChangeHandler() {
		columns.forEach( item -> { });	
		this.columnListChangeHandler.setOriginalList(columns);
	}

	
	public boolean isHasToken() {
		return StringUtils.hasText(accessToken);
	}
}
