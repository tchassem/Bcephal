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
public class UserEditorData extends EditorData<User> {

	private List<Nameable> profiles;
	
	private List<String> languages;

	public UserEditorData() {
		profiles = new ArrayList<>(0);
		languages = new ArrayList<>(0);
	}
	
}