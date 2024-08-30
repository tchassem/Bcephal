package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

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

@Entity(name = "UserLoad")
@Table(name = "BCP_USER_LOAD")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoad  extends MainObject {

	private static final long serialVersionUID = -6568121688228850559L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_load_seq")
	@SequenceGenerator(name = "user_load_seq", sequenceName = "user_load_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private Long loaderId;
	
	private String username;
	
	@Enumerated(EnumType.STRING)
	private UserLoaderTreatment treatment;
	
	@Enumerated(EnumType.STRING)
	private RunModes mode;

	@Enumerated(EnumType.STRING)
	private RunStatus status;
	
	private int fileCount;

	private int emptyFileCount;

	private int errorFileCount;

	private int loadedFileCount;

	private boolean error;

	private String message;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Date startDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Date endDate;
	
	private String operationCode;
	
	@Transient
	private String filesDir;
	
	@Transient
	private List<UserLoadLog> logs = new ArrayList<>();

	@Override
	public UserLoad copy() {
		return null;
	}
	
	
}
