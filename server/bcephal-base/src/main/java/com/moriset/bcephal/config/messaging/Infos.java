package com.moriset.bcephal.config.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Infos {
	

	public static final String MAIL = "MAIL";
	public static final String SMS = "SMS";
	
	private Object message;
	private String destination;
}
