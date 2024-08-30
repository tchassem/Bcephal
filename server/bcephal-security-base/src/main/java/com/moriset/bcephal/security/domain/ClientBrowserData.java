/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClientBrowserData extends BrowserData{

	private String description;
	
	private ClientStatus status;
	
	private boolean defaultClient;
	
	public ClientBrowserData(ClientBaseObject item){
		super(item.getId(), item.getName(), true, item.getCreationDate(), item.getModificationDate());
		setDescription(item.getDescription());
		setStatus(item.getStatus());
		setDefaultClient(item.isDefaultClient());
	}
}