package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;

@Data
public class AccessRightEditorData {

	private ListChangeHandler<ProfileProject> itemListChangeHandler;

	private List<Nameable> profiles;

	public AccessRightEditorData() {
        this.itemListChangeHandler = new ListChangeHandler<ProfileProject>();
        this.profiles = new ArrayList<Nameable>();
    }
	
}
