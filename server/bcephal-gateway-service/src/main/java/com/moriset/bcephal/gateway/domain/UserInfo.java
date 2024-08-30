/**
 * 
 */
package com.moriset.bcephal.gateway.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfo {

	private String login;
	private String firstName;
	private String name;
	private String defaultLanguage;
	
}
