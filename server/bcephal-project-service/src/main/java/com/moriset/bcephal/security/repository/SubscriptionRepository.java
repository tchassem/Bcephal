/**
 * 
 */
package com.moriset.bcephal.security.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.Subscription;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface SubscriptionRepository extends PersistentRepository<Subscription> {

	Subscription findByName(String name);

}
