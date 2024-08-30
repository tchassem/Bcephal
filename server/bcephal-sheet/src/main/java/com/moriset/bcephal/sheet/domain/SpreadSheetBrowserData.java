/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import com.moriset.bcephal.domain.BrowserData;

/**
 * @author Joseph Wambo
 *
 */
public class SpreadSheetBrowserData extends BrowserData {

	public SpreadSheetBrowserData(SpreadSheet spreadSheet) {
		super(spreadSheet.getId(), spreadSheet.getName(),
				spreadSheet.getGroup() != null ? spreadSheet.getGroup().getName() : null,
				spreadSheet.isVisibleInShortcut(), spreadSheet.getCreationDate(), spreadSheet.getModificationDate());

	}

}
