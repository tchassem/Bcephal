package com.moriset.bcephal.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class LoaderData {
	
	private Long loaderId;
	private String loaderName;
	private String loadNbr;
	private String operationCode;
	private Date loadDate;
	private String username;
	private RunModes mode;
	private List<String> files;	
	
	private String loaderFileColumn;
	private String loaderNbrColumn;
	private String operationCodeColumn;
	
	public LoaderData() {
		files = new ArrayList<>();
	}
	
	public static LoaderData NewInstance() {
		return new LoaderData();
	}
	
}
