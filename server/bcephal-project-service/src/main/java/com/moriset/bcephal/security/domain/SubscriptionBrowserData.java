/**
 * 
 */
package com.moriset.bcephal.security.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SubscriptionBrowserData")
@Table(name = "BCP_SEC_SUBSCRIPTION")
@Data
public class SubscriptionBrowserData {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_subscription_seq")
	@SequenceGenerator(name = "sec_subscription_seq", sequenceName = "sec_subscription_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull(message = "{subscription.name.validation.null.message}")
	@Size(min = 3, max = 100, message = "{subscription.name.validation.size.message}")
	private String name;

	private boolean defaultSubscription;

}
