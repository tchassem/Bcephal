/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.SubscriptionBrowserData;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface SubscriptionBrowserDataRepository extends JpaRepository<SubscriptionBrowserData, Long> {

	List<SubscriptionBrowserData> findByDefaultSubscription(boolean defaultSubscription);

}
