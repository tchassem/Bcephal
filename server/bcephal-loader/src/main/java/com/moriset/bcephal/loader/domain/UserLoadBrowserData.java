package com.moriset.bcephal.loader.domain;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserLoadBrowserData extends BrowserData {
	
	private RunStatus status;
	
	private int fileCount;

	private int emptyFileCount;

	private int errorFileCount;

	private int loadedFileCount;
	
	private String username;
	
	private RunModes mode;

	private boolean error;

	private String message;

	@Enumerated(EnumType.STRING)
	private UserLoaderTreatment treatment;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Date startDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Date endDate;

	public UserLoadBrowserData(UserLoad userLoad) {
		super(userLoad);
		this.status = userLoad.getStatus();
		this.fileCount = userLoad.getFileCount();
		this.emptyFileCount = userLoad.getEmptyFileCount();
		this.errorFileCount = userLoad.getErrorFileCount();
		this.loadedFileCount  = userLoad.getLoadedFileCount();
		this.error = userLoad.isError();
		this.message = userLoad.getMessage();
		this.startDate = userLoad.getStartDate();
		this.endDate = userLoad.getEndDate();
		this.treatment = userLoad.getTreatment();
		this.username = userLoad.getUsername();
		this.mode = userLoad.getMode();
	}
	
}
