package com.moriset.bcephal.manager.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileManagerBrowserData {
    
	private String code;
	
	private String name;

	private String category;

	private String path;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp modificationDate;
	
	public FileManagerBrowserData(String code, String name, String category, Timestamp modificationDate) {
		this.name = name;
		this.category = category;
		this.code = code;
		this.modificationDate = modificationDate;
	}
	
	public FileManagerBrowserData(String code, String name, String category, String path, Timestamp modificationDate) {
		this.name = name;
		this.path = path;
		this.category = category;
		this.code = code;
		this.modificationDate = modificationDate;
	}

}
