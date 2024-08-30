/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Moriset
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	private String code;
	
	private String name;
	
	private Long clientId;
		
}
