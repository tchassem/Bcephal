/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.security.domain.Subscription.SubscriptionStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SubscriptionCustomBrowserData extends  BrowserData{
	
	private int maxUser;

	private String ownerUser;

	private SubscriptionStatus status;

	private boolean defaultSubscription;
	
	public SubscriptionCustomBrowserData(Subscription item) {
		super(item.getId(),item.getName());
		maxUser = item.getMaxUser();
		ownerUser = item.getOwnerUser();
		status = item.getStatus();
		defaultSubscription = item.isDefaultSubscription();
	}

}
