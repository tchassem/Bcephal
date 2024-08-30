package com.moriset.bcephal.loader.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserLoadData {

	private UserLoad userLoad;	
	
	private String filesDir;
	
	@JsonIgnore
	private UserLoader userLoader;
	
	@JsonIgnore
	private Long userLoaderId;
	
	
	
}
