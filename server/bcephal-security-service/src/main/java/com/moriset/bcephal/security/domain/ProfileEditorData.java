/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ProfileEditorData extends EditorData<Profile> {

	private List<Nameable> users;
	private List<Nameable> roles;
	private List<ClientFunctionality> functionalities;	
	private List<ProfileType> types;

	public ProfileEditorData() {
		types = new ArrayList<>(0);
		users = new ArrayList<>(0);
		roles = new ArrayList<>(0);
		functionalities = new ArrayList<>(0);
	}
	
}
