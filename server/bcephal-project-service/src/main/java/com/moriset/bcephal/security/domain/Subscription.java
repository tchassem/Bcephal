/**
 * 
 */
package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.IPersistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * B-CEPAHL Subscription
 * 
 * @author Joseph Wambo
 *
 */
@Entity(name = "Subscription")
@Table(name = "BCP_SEC_SUBSCRIPTION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription implements IPersistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7788870694339997099L;

	@Transient
	public static String DEFAULT_SUBSCRIPTION_NAME = "Default";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_subscription_seq")
	@SequenceGenerator(name = "sec_subscription_seq", sequenceName = "sec_subscription_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull(message = "{subscription.name.validation.null.message}")
	@Size(min = 3, max = 100, message = "{subscription.name.validation.size.message}")
	private String name;

	private int maxUser;

	private String ownerUser;

	@Enumerated(EnumType.STRING)
	public SubscriptionStatus status;

	private boolean defaultSubscription;

	public enum SubscriptionStatus {
		ACTIVE, TRIAL, SUSPENDED, END_OF_TRIAL, CLOSED
	}
}
