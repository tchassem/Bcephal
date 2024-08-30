/**
 * 
 */
package com.moriset.bcephal.security.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Joseph Wambo
 *
 */

public class AddressTests {

	@Test
	void validateAddress() throws JsonProcessingException {
		Address address = buildAddress();
		assertNotNull(address);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(address);
		System.out.println(address);
		System.out.println();
		System.out.println(json);
	}
	
	Address buildAddress() {
		Address address = new Address("joseph@moriset.com", "237 15455255", "1254 Rue des poulets","12564", "Yaoundé", "Cameroun");		
		return address;
	}
	
}
