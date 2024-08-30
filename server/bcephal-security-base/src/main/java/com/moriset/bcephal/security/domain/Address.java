/**
 * 
 */
package com.moriset.bcephal.security.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Address
 * @author Joseph Wambo
 *
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address {

	@Email(message = "{email.validation.message}")
	private String email;
	private String phone;	
	private String street;
	private String postalCode;
	private String city;
	private String country;
	
}
