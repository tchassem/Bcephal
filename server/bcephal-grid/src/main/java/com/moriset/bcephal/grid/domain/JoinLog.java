/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinLog")
@Table(name = "BCP_JOIN_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinLog extends MainObject {

	private static final long serialVersionUID = 5931565127646645639L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_log_seq")
	@SequenceGenerator(name = "join_log_seq", sequenceName = "join_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long joinId;
	
	@Enumerated(EnumType.STRING) 
	private JoinPublicationMethod publicationMethod;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType publicationGridType;
	
	@Transient
	private Long publicationGridId;
	
	private String publicationGridName;
	
	private String publicationNumber;
	
	private Long publicationNbrAttributeId;	
	
	private String publicationNbrAttributeName;
	
	private String operationCode;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = true)
	private Timestamp endDate;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
	
	@Enumerated(EnumType.STRING) 
	private RunModes mode;
		
	@Column(name = "username")
	private String user;
		
	private long rowCount;
	
	private String message;
	
	
	
	public JoinLog() {
		this.status = RunStatus.IN_PROGRESS;
		this.user = "B-CEPHAL";
		this.mode = RunModes.M;
		this.status = RunStatus.IN_PROGRESS;
		this.rowCount = 0;
	}
	
	public JoinLog(Join join) {
		this();
		this.joinId = join.getId();
		setName(join.getName());
		this.publicationMethod = join.getPublicationMethod();
		this.publicationGridType = join.getPublicationDataSourceType();
		this.publicationGridId = join.getPublicationGridId();
		this.publicationGridName = join.getPublicationGridName();
		this.publicationNbrAttributeId = join.getPublicationRunAttributeId();
	}

	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
