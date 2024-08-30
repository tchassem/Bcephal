/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ProfileBrowserData extends BrowserData {

	private String description;
	
	public ProfileBrowserData(Profile item){
		super(item.getId(), item.getName(), true, item.getCreationDate(), item.getModificationDate());
		setDescription(item.getDescription());
	}
	
}
