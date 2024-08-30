package com.moriset.bcephal.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupBrowserData extends BrowserData {

	public GroupBrowserData(BGroup group) {
		super(group.getId(), group.getName(), group.isVisibleInShortcut(), group.getCreationDate(),
				group.getModificationDate());
	}
}
