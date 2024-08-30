/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserBrowserData extends BrowserData{

	private String username;
	private boolean enabled;
    private String firstName;
    private String lastName;
    private String email;
	
	public UserBrowserData(User item){
		super(item.getId(), item.getName(), true, item.getCreationDate(), item.getModificationDate());
		setUsername(item.getUsername());
		setEnabled(item.isEnabled());
		setFirstName(item.getFirstName());
		setLastName(item.getLastName());
		setEmail(item.getEmail());
	}

}
