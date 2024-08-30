/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinBrowserData extends BrowserData {
		
	private boolean materialized;

	public JoinBrowserData(Join join) {
		super(join);
		materialized = join.isPublished();
		setConfirmAction(join.isConfirmAction());
	}
	
}
