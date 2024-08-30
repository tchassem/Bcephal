package com.moriset.bcephal.utils;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data
@jakarta.persistence.MappedSuperclass
public class SlimObject {
	@jakarta.persistence.Id 
	private Long Id;
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String name;
	
	@Override
	public String toString() {
		return name;
	}
	
}
